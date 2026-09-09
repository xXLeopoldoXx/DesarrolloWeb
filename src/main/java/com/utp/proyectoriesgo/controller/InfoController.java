package com.utp.proyectoriesgo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InfoController {

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "proyecto", "Sistema de Apoyo y Evaluación de Riesgo",
            "curso", "Desarrollo Web Integrado",
            "semana", "Semana 2",
            "integrantes", List.of("Anheli", "Naty", "Eduardo", "Mari", "Leopoldo")
        );
    }
}