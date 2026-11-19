
package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ReporteRequest;
import com.utp.proyectoriesgo.model.Reporte;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.ReporteService;
import com.utp.proyectoriesgo.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comunidad/publicaciones/{publicacionId}/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioService usuarioService;

    public ReporteController(
            ReporteService reporteService,
            UsuarioService usuarioService) {

        this.reporteService = reporteService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<String> reportar(
            @PathVariable Long publicacionId,
            @Valid @RequestBody ReporteRequest request,
            Authentication autenticacion) {

        Usuario autor = usuarioService.actual(autenticacion);

        Reporte reporte = reporteService.registrarReporte(
                publicacionId,
                request.motivo(),
                autor
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Reporte registrado correctamente. ID: "
                        + reporte.getId());
    }
}
