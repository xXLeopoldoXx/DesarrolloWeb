package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.repository.ComentarioRepository;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.ReaccionRepository;
import com.utp.proyectoriesgo.repository.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Parte de "Comunidad" en "Eliminar mis datos" (privacidad).
 *
 * El nombre del bean ("comunidadService") es parte del contrato: DisponibilidadBorradoCuenta solo habilita
 * el borrado de cuenta si existe una limpieza con ese nombre. No lo renombres.
 */
@Service
public class ComunidadService implements LimpiezaDatosUsuaria {

    private final ReaccionRepository reacciones;
    private final ReporteRepository reportes;
    private final ComentarioRepository comentarios;
    private final PublicacionRepository publicaciones;

    public ComunidadService(ReaccionRepository reacciones,
                            ReporteRepository reportes,
                            ComentarioRepository comentarios,
                            PublicacionRepository publicaciones) {
        this.reacciones = reacciones;
        this.reportes = reportes;
        this.comentarios = comentarios;
        this.publicaciones = publicaciones;
    }

    /**
     * El orden importa por las llaves foráneas: primero reacciones, reportes y comentarios (todos apuntan a
     * publicaciones), al final las publicaciones.
     */
    @Override
    @Transactional
    public void eliminarDatosDe(Long usuarioId) {
        reacciones.eliminarDeUsuaria(usuarioId);
        reportes.eliminarDeUsuaria(usuarioId);
        comentarios.eliminarDeUsuaria(usuarioId);
        publicaciones.eliminarDeUsuaria(usuarioId);
    }
}
