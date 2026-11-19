
package com.utp.proyectoriesgo.service;

import com.utp.proyectoriesgo.exception.RecursoNoEncontradoException;
import com.utp.proyectoriesgo.model.Publicacion;
import com.utp.proyectoriesgo.model.Reporte;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.repository.PublicacionRepository;
import com.utp.proyectoriesgo.repository.ReporteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final PublicacionRepository publicacionRepository;

    public ReporteService(
            ReporteRepository reporteRepository,
            PublicacionRepository publicacionRepository) {

        this.reporteRepository = reporteRepository;
        this.publicacionRepository = publicacionRepository;
    }

    @Transactional
    public Reporte registrarReporte(
            Long publicacionId,
            String motivo,
            Usuario autor) {

        // 1. Validar el motivo
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo del reporte es obligatorio");
        }

        String motivoLimpio = motivo.trim();

        if (motivoLimpio.length() > 300) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar 300 caracteres");
        }

        // 2. Validar la usuaria
        if (autor == null || autor.getId() == null) {
            throw new IllegalArgumentException(
                    "Se necesita una usuaria autenticada");
        }

        // 3. Buscar la publicación
        Publicacion publicacion = publicacionRepository
                .findById(publicacionId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("La publicación no existe"));

        // 4. Comprobar si está oculta
        if (publicacion.isOculta()) {
            throw new IllegalArgumentException(
                    "No se puede reportar una publicación oculta");
        }

        // 5. Evitar reportes duplicados
        boolean yaReportada = reporteRepository
                .existsByPublicacionIdAndAutorId(
                        publicacionId,
                        autor.getId());

        if (yaReportada) {
            throw new IllegalArgumentException(
                    "Ya reportaste esta publicación");
        }

        // 6. Registrar el reporte
        Reporte reporte = new Reporte();
        reporte.setPublicacion(publicacion);
        reporte.setAutor(autor);
        reporte.setMotivo(motivoLimpio);

        return reporteRepository.save(reporte);
    }

    // Reportes pendientes para moderación (los de publicaciones ya ocultas no se muestran)
    @Transactional(readOnly = true)
    public List<Reporte> listarReportes() {
        return reporteRepository.findByPublicacionOcultaFalseOrderByFechaDesc();
    }

}
