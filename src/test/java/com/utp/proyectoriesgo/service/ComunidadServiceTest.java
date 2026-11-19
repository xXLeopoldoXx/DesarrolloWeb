package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.repository.ComentarioRepository;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.ReporteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class ComunidadServiceTest {

    private final ReporteRepository reportes = mock(ReporteRepository.class);
    private final ComentarioRepository comentarios = mock(ComentarioRepository.class);
    private final PublicacionRepository publicaciones = mock(PublicacionRepository.class);
    private final ComunidadService servicio = new ComunidadService(reportes, comentarios, publicaciones);

    @Test
    void esUnaLimpiezaDeDatos_paraQueSePuedaBorrarLaCuenta() {
        assertInstanceOf(LimpiezaDatosUsuaria.class, servicio);
    }

    @Test
    void eliminarDatos_borraPrimeroReportesYComentariosYAlFinalLasPublicaciones() {
        servicio.eliminarDatosDe(5L);

        // El orden evita violar las llaves foráneas (reportes y comentarios apuntan a publicaciones).
        InOrder orden = inOrder(reportes, comentarios, publicaciones);
        orden.verify(reportes).eliminarDeUsuaria(5L);
        orden.verify(comentarios).eliminarDeUsuaria(5L);
        orden.verify(publicaciones).eliminarDeUsuaria(5L);
    }

    @Test
    void elNombreDeLaClaseDaElBeanQueExigeElBorradoDeCuenta() {
        // DisponibilidadBorradoCuenta busca la limpieza "comunidadService": no renombrar la clase.
        assertEquals("ComunidadService", ComunidadService.class.getSimpleName());
    }
}
