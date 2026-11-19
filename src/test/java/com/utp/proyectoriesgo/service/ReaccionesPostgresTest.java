package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.dto.ReaccionesResponse;
import com.utp.proyectoriesgo.model.Comentario;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.TipoReaccion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.ComentarioRepository;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reacciones y comentarios agregados contra la base real (se revierte al terminar: @Transactional):
 * alternar, totales, reacciones propias y «Eliminar mis datos» sin violar llaves foráneas.
 */
@SpringBootTest
@Transactional
class ReaccionesPostgresTest {

    @Autowired
    private ReaccionService reacciones;
    @Autowired
    private ComunidadService comunidad;
    @Autowired
    private ComentarioService comentarios;
    @Autowired
    private UsuarioRepository usuarias;
    @Autowired
    private PublicacionRepository publicaciones;
    @Autowired
    private ComentarioRepository comentariosRepo;
    @Autowired
    private EntityManager entityManager;

    private Usuario usuaria(String prefijo) {
        String nombre = prefijo + UUID.randomUUID().toString().substring(0, 8);
        return usuarias.save(new Usuario(nombre, "hash-de-prueba", true));
    }

    private Publicacion publicacion(Usuario autor, String texto) {
        Publicacion publicacion = new Publicacion();
        publicacion.setAutor(autor);
        publicacion.setTexto(texto);
        return publicaciones.save(publicacion);
    }

    @Test
    void alternarPoneYQuitaYLosTotalesSeSumanPorUsuaria() {
        Usuario ana = usuaria("ana");
        Usuario eva = usuaria("eva");
        Publicacion deAna = publicacion(ana, "hola");

        reacciones.alternar(eva.getId(), deAna.getId(), TipoReaccion.ABRAZO);
        ReaccionesResponse tras = reacciones.alternar(ana.getId(), deAna.getId(), TipoReaccion.ABRAZO);
        entityManager.flush();
        entityManager.clear();

        assertEquals(2L, tras.conteos().get(TipoReaccion.ABRAZO));
        assertEquals(List.of(TipoReaccion.ABRAZO), tras.mias());

        // Eva lo quita: queda solo la de Ana, y para Eva ya no es suya.
        ReaccionesResponse trasQuitar = reacciones.alternar(eva.getId(), deAna.getId(), TipoReaccion.ABRAZO);
        assertEquals(1L, trasQuitar.conteos().get(TipoReaccion.ABRAZO));
        assertTrue(trasQuitar.mias().isEmpty());
        assertEquals(0L, trasQuitar.conteos().get(TipoReaccion.FUERZA));
    }

    @Test
    void resumenesYConteoDeComentariosSeCalculanParaVariasPublicacionesDeUnaVez() {
        Usuario ana = usuaria("ana");
        Usuario eva = usuaria("eva");
        Publicacion p1 = publicacion(ana, "uno");
        Publicacion p2 = publicacion(ana, "dos");

        reacciones.alternar(eva.getId(), p1.getId(), TipoReaccion.IDENTIFICO);
        reacciones.alternar(eva.getId(), p1.getId(), TipoReaccion.FUERZA);
        comentarios.crearComentario(p1.getId(), "ánimo", eva);
        comentarios.crearComentario(p1.getId(), "gracias", ana);
        entityManager.flush();
        entityManager.clear();

        Map<Long, ReaccionesResponse> resumen = reacciones.resumenes(eva.getId(), List.of(p1.getId(), p2.getId()));
        Map<Long, Long> cantidades = comentarios.contarVisibles(List.of(p1.getId(), p2.getId()));

        assertEquals(1L, resumen.get(p1.getId()).conteos().get(TipoReaccion.IDENTIFICO));
        assertEquals(2, resumen.get(p1.getId()).mias().size());
        assertTrue(resumen.get(p2.getId()).mias().isEmpty());
        assertEquals(2L, cantidades.get(p1.getId()));
        assertEquals(null, cantidades.get(p2.getId()));
    }

    @Test
    void losComentariosOcultosNoSeCuentan() {
        Usuario ana = usuaria("ana");
        Publicacion p = publicacion(ana, "hola");
        comentarios.crearComentario(p.getId(), "visible", ana);
        Comentario oculto = comentarios.crearComentario(p.getId(), "oculto", ana);
        oculto.setOculto(true);
        comentariosRepo.save(oculto);
        entityManager.flush();
        entityManager.clear();

        assertEquals(1L, comentarios.contarVisibles(List.of(p.getId())).get(p.getId()));
    }

    @Test
    void eliminarMisDatosBorraSusReaccionesYLasRecibidasSinTocarLasDeOtras() {
        Usuario ana = usuaria("ana");
        Usuario eva = usuaria("eva");
        Publicacion deAna = publicacion(ana, "de ana");
        Publicacion deEva = publicacion(eva, "de eva");
        reacciones.alternar(eva.getId(), deAna.getId(), TipoReaccion.ABRAZO);   // Eva reaccionó a Ana
        reacciones.alternar(ana.getId(), deEva.getId(), TipoReaccion.FUERZA);   // Ana reaccionó a Eva
        reacciones.alternar(eva.getId(), deEva.getId(), TipoReaccion.ABRAZO);   // Eva a la suya
        entityManager.flush();

        comunidad.eliminarDatosDe(ana.getId());   // no debe violar llaves foráneas
        entityManager.flush();
        entityManager.clear();

        // De Eva queda solo su propia reacción; la de Ana desapareció y las que recibió Ana también.
        ReaccionesResponse sobreEva = reacciones.resumenes(eva.getId(), List.of(deEva.getId())).get(deEva.getId());
        assertEquals(1L, sobreEva.conteos().get(TipoReaccion.ABRAZO));
        assertEquals(0L, sobreEva.conteos().get(TipoReaccion.FUERZA));
        assertTrue(publicaciones.findById(deAna.getId()).isEmpty());
    }

    @Test
    void esIdempotente_sinReaccionesNoFalla() {
        Usuario ana = usuaria("ana");

        comunidad.eliminarDatosDe(ana.getId());
        comunidad.eliminarDatosDe(ana.getId());

        assertTrue(reacciones.resumenes(ana.getId(), List.of()).isEmpty());
    }
}
