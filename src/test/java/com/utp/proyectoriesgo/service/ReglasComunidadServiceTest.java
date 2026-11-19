package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.ComentarioRepository;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.ReporteRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Reglas de negocio de publicaciones, comentarios y reportes (moderación incluida). */
class ReglasComunidadServiceTest {

    private final PublicacionRepository publicaciones = mock(PublicacionRepository.class);
    private final ComentarioRepository comentarios = mock(ComentarioRepository.class);
    private final ReporteRepository reportes = mock(ReporteRepository.class);

    private final PublicacionService publicacionService = new PublicacionService(publicaciones);
    private final ComentarioService comentarioService = new ComentarioService(comentarios, publicaciones);
    private final ReporteService reporteService = new ReporteService(reportes, publicaciones);

    private Usuario usuaria(long id) {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(id);
        return usuario;
    }

    private Publicacion visible() {
        Publicacion publicacion = new Publicacion();
        publicacion.setTexto("Hola");
        return publicacion;
    }

    private Publicacion oculta() {
        Publicacion publicacion = visible();
        publicacion.setOculta(true);
        return publicacion;
    }

    // ---------- Publicaciones ----------

    @Test
    void crearPublicacion_recortaElTextoYNaceVisible() {
        when(publicaciones.save(any(Publicacion.class))).thenAnswer(i -> i.getArgument(0));

        Publicacion creada = publicacionService.crearPublicacion("  Hola a todas  ", usuaria(1));

        assertEquals("Hola a todas", creada.getTexto());
        assertFalse(creada.isOculta());
    }

    @Test
    void crearPublicacion_rechazaVacioYMasDe500Caracteres() {
        assertThrows(IllegalArgumentException.class, () -> publicacionService.crearPublicacion("   ", usuaria(1)));
        assertThrows(IllegalArgumentException.class,
                () -> publicacionService.crearPublicacion("a".repeat(501), usuaria(1)));
        verify(publicaciones, never()).save(any());
    }

    @Test
    void ocultar_marcaLaPublicacionYNoSePuedeOcultarDosVeces() {
        Publicacion publicacion = visible();
        when(publicaciones.findById(1L)).thenReturn(Optional.of(publicacion));

        publicacionService.ocultarPublicacion(1L);

        assertTrue(publicacion.isOculta());
        assertThrows(IllegalArgumentException.class, () -> publicacionService.ocultarPublicacion(1L));
    }

    @Test
    void ocultar_unaInexistente_esUn404() {
        when(publicaciones.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> publicacionService.ocultarPublicacion(9L));
    }

    // ---------- Comentarios ----------

    @Test
    void comentar_enUnaPublicacionOculta_noSeGuardaNada() {
        when(publicaciones.findById(1L)).thenReturn(Optional.of(oculta()));

        assertThrows(IllegalArgumentException.class, () -> comentarioService.crearComentario(1L, "Hola", usuaria(2)));
        verify(comentarios, never()).save(any());
    }

    @Test
    void listarComentarios_deUnaPublicacionOcultaOInexistente_esUn404() {
        when(publicaciones.findById(1L)).thenReturn(Optional.of(oculta()));
        when(publicaciones.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> comentarioService.listarComentarios(1L));
        assertThrows(RecursoNoEncontradoException.class, () -> comentarioService.listarComentarios(2L));
    }

    // ---------- Reportes ----------

    @Test
    void reportar_noPermiteElMismoReporteDosVeces() {
        when(publicaciones.findById(1L)).thenReturn(Optional.of(visible()));
        when(reportes.existsByPublicacionIdAndAutorId(1L, 2L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> reporteService.registrarReporte(1L, "Spam", usuaria(2)));
        verify(reportes, never()).save(any());
    }

    @Test
    void reportar_unaPublicacionOcultaOInexistente() {
        when(publicaciones.findById(1L)).thenReturn(Optional.of(oculta()));
        when(publicaciones.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reporteService.registrarReporte(1L, "Spam", usuaria(2)));
        assertThrows(RecursoNoEncontradoException.class, () -> reporteService.registrarReporte(2L, "Spam", usuaria(2)));
    }

    @Test
    void listarReportes_pideSoloLosDePublicacionesVisibles() {
        reporteService.listarReportes();

        verify(reportes).findByPublicacionOcultaFalseOrderByFechaDesc();
    }
}
