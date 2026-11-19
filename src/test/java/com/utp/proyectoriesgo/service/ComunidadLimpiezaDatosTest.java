package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.model.Comentario;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Reporte;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.ComentarioRepository;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.ReporteRepository;
import com.utp.proyectoriesgo.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * "Eliminar mis datos" en Comunidad, contra la base real (se revierte al terminar: @Transactional).
 * Comprueba que se borra TODO lo de la usuaria sin tocar lo de otra y sin violar llaves foráneas.
 */
@SpringBootTest
@Transactional
class ComunidadLimpiezaDatosTest {

    @Autowired
    private ComunidadService comunidad;
    @Autowired
    private UsuarioRepository usuarias;
    @Autowired
    private PublicacionRepository publicaciones;
    @Autowired
    private ComentarioRepository comentarios;
    @Autowired
    private ReporteRepository reportes;
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

    private void comentario(Publicacion publicacion, Usuario autor) {
        Comentario comentario = new Comentario();
        comentario.setPublicacion(publicacion);
        comentario.setAutor(autor);
        comentario.setTexto("comentario");
        comentarios.save(comentario);
    }

    private void reporte(Publicacion publicacion, Usuario autor) {
        Reporte reporte = new Reporte();
        reporte.setPublicacion(publicacion);
        reporte.setAutor(autor);
        reporte.setMotivo("motivo");
        reportes.save(reporte);
    }

    @Test
    void borraTodoLoDeLaUsuariaYNadaDeLaOtra() {
        Usuario ana = usuaria("ana");
        Usuario eva = usuaria("eva");

        Publicacion deAna = publicacion(ana, "de ana");
        Publicacion deEva = publicacion(eva, "de eva");
        comentario(deEva, ana);   // Ana comentó en la publicación de Eva
        comentario(deAna, eva);   // Eva comentó en la publicación de Ana
        reporte(deEva, ana);      // Ana reportó a Eva
        reporte(deAna, eva);      // Eva reportó a Ana
        entityManager.flush();

        long publicacionesAntes = publicaciones.count();
        long comentariosAntes = comentarios.count();
        long reportesAntes = reportes.count();

        comunidad.eliminarDatosDe(ana.getId());
        entityManager.flush();
        entityManager.clear();

        // Se va: la publicación de Ana, su comentario en la de Eva, el comentario de Eva en la de Ana
        // y los dos reportes (el que hizo Ana y el que recibió su publicación).
        assertEquals(publicacionesAntes - 1, publicaciones.count());
        assertEquals(comentariosAntes - 2, comentarios.count());
        assertEquals(reportesAntes - 2, reportes.count());
        // Lo de Eva sigue ahí.
        assertEquals(true, publicaciones.findById(deEva.getId()).isPresent());
    }

    @Test
    void esIdempotente_sinDatosNoFalla() {
        Usuario sinNada = usuaria("sola");

        comunidad.eliminarDatosDe(sinNada.getId());
        comunidad.eliminarDatosDe(sinNada.getId());
    }
}
