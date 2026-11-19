package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.AutoevaluacionRequest;
import com.utp.proyectoriesgo.dto.RespuestaItem;
import com.utp.proyectoriesgo.dto.ResultadoResponse;
import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Factor;
import com.utp.proyectoriesgo.model.Respuesta;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * «Mi evaluación» contra la base real (se revierte al terminar: @Transactional). Comprueba que guardar,
 * ver, eliminar y «Eliminar mis datos» funcionan sin violar llaves foráneas y sin tocar lo de otra usuaria.
 */
@SpringBootTest
@Transactional
class AutoevaluacionLimpiezaDatosTest {

    @Autowired
    private AutoevaluacionService autoevaluacion;
    @Autowired
    private UsuarioRepository usuarias;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private Map<String, LimpiezaDatosUsuaria> limpiezas;

    private Usuario usuaria(String prefijo) {
        String nombre = prefijo + UUID.randomUUID().toString().substring(0, 8);
        return usuarias.save(new Usuario(nombre, "hash-de-prueba", true));
    }

    private static AutoevaluacionRequest solicitud(Respuesta respuesta) {
        List<RespuestaItem> items = new ArrayList<>();
        for (Factor factor : Factor.values()) {
            items.add(new RespuestaItem(factor, respuesta));
        }
        return new AutoevaluacionRequest(items);
    }

    private long respuestasGuardadas(Long evaluacionId) {
        return entityManager.createQuery(
                        "select count(r) from RespuestaEvaluacion r where r.evaluacionId = :id", Long.class)
                .setParameter("id", evaluacionId)
                .getSingleResult();
    }

    @Test
    void guardaYRecuperaLaEvaluacionConSusRespuestas() {
        Usuario ana = usuaria("ana");

        ResultadoResponse creado = autoevaluacion.crear(ana.getId(), solicitud(Respuesta.SI));
        entityManager.flush();
        entityManager.clear();

        ResultadoResponse detalle = autoevaluacion.detalle(ana.getId(), creado.id());
        assertEquals(140, detalle.puntaje());
        assertEquals("MUY_ALTO", detalle.nivel());
        assertEquals(9, detalle.senales().size());
        assertEquals(1, autoevaluacion.listar(ana.getId()).size());
        assertEquals(9, respuestasGuardadas(creado.id()));
    }

    @Test
    void eliminarUnaEvaluacionBorraSusRespuestasYSoloEsa() {
        Usuario ana = usuaria("ana");
        ResultadoResponse primera = autoevaluacion.crear(ana.getId(), solicitud(Respuesta.NO));
        ResultadoResponse segunda = autoevaluacion.crear(ana.getId(), solicitud(Respuesta.NO_SEGURA));
        entityManager.flush();

        autoevaluacion.eliminar(ana.getId(), primera.id());
        entityManager.flush();
        entityManager.clear();

        assertEquals(0, respuestasGuardadas(primera.id()));
        assertEquals(9, respuestasGuardadas(segunda.id()));
        assertEquals(1, autoevaluacion.listar(ana.getId()).size());
        assertThrows(RecursoNoEncontradoException.class, () -> autoevaluacion.detalle(ana.getId(), primera.id()));
    }

    @Test
    void unaUsuariaNoPuedeVerNiBorrarLaEvaluacionDeOtra() {
        Usuario ana = usuaria("ana");
        Usuario berta = usuaria("berta");
        ResultadoResponse deAna = autoevaluacion.crear(ana.getId(), solicitud(Respuesta.SI));
        entityManager.flush();

        assertThrows(RecursoNoEncontradoException.class, () -> autoevaluacion.detalle(berta.getId(), deAna.id()));
        assertThrows(RecursoNoEncontradoException.class, () -> autoevaluacion.eliminar(berta.getId(), deAna.id()));
        assertTrue(autoevaluacion.listar(berta.getId()).isEmpty());
        assertEquals(9, respuestasGuardadas(deAna.id()));
    }

    @Test
    void eliminarMisDatosBorraTodoLoDeLaUsuariaYNadaDeLaOtra() {
        Usuario ana = usuaria("ana");
        Usuario berta = usuaria("berta");
        ResultadoResponse a1 = autoevaluacion.crear(ana.getId(), solicitud(Respuesta.SI));
        ResultadoResponse a2 = autoevaluacion.crear(ana.getId(), solicitud(Respuesta.NO));
        ResultadoResponse deBerta = autoevaluacion.crear(berta.getId(), solicitud(Respuesta.NO_SEGURA));
        entityManager.flush();

        autoevaluacion.eliminarDatosDe(ana.getId());
        entityManager.flush();
        entityManager.clear();

        assertTrue(autoevaluacion.listar(ana.getId()).isEmpty());
        assertEquals(0, respuestasGuardadas(a1.id()));
        assertEquals(0, respuestasGuardadas(a2.id()));
        assertEquals(1, autoevaluacion.listar(berta.getId()).size());
        assertEquals(9, respuestasGuardadas(deBerta.id()));
    }

    @Test
    void esIdempotente_sinDatosNoFalla() {
        Usuario ana = usuaria("ana");

        autoevaluacion.eliminarDatosDe(ana.getId());
        autoevaluacion.eliminarDatosDe(ana.getId());

        assertTrue(autoevaluacion.listar(ana.getId()).isEmpty());
    }

    @Test
    void elBeanSeLlamaAutoevaluacionServiceComoLoExigeElBorradoDeCuenta() {
        assertTrue(limpiezas.containsKey("autoevaluacionService"));
    }
}
