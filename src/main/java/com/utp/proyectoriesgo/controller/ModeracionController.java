
package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ReporteComunidadResponse;
import com.utp.proyectoriesgo.model.Reporte;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.ReporteService;
import com.utp.proyectoriesgo.service.PublicacionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/comunidad")
public class ModeracionController {

    private final ReporteService reporteService;
    private final PublicacionService publicacionService;

    public ModeracionController(
            ReporteService reporteService,
            PublicacionService publicacionService) {

        this.reporteService = reporteService;
        this.publicacionService = publicacionService;
    }

    // =========================================
    // 1. LISTAR REPORTES DE LA COMUNIDAD
    // =========================================

    @GetMapping("/reportes")
    public List<ReporteComunidadResponse> listarReportes() {

        return reporteService.listarReportes()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    // Convertir el reporte a una respuesta segura
    private ReporteComunidadResponse convertirRespuesta(
            Reporte reporte) {

        Usuario autorPublicacion =
                reporte.getPublicacion().getAutor();

        String seudonimo = autorPublicacion.getSeudonimo();

        if (seudonimo == null || seudonimo.isBlank()) {
            seudonimo = "Anónima";
        }

        return new ReporteComunidadResponse(
                reporte.getId(),
                reporte.getPublicacion().getId(),
                seudonimo,
                reporte.getPublicacion().getTexto(),
                reporte.getMotivo(),
                reporte.getFecha()
        );
    }

    // =========================================
    // 2. OCULTAR PUBLICACIÓN REPORTADA
    // =========================================

    @PatchMapping("/publicaciones/{id}/ocultar")
    public ResponseEntity<Void> ocultarPublicacion(
            @PathVariable Long id) {

        publicacionService.ocultarPublicacion(id);

        return ResponseEntity.noContent().build();
    }
}
