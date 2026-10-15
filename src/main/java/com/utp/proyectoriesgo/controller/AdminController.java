package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ReporteResponse;
import com.utp.proyectoriesgo.service.EvaluacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final EvaluacionService evaluacionService;

    public AdminController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @GetMapping("/reporte")
    public ReporteResponse reporte() {
        return evaluacionService.generarReporte();
    }
}
