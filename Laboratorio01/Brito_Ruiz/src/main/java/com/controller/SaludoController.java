package com.utp.semana1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.utp.semana1.model.ProyectoInfo;
import com.utp.semana1.model.EstadoInfo;

@RestController
@RequestMapping("/api")
public class SaludoController {

    // Parte E: Endpoint básico
    @GetMapping("/saludo")
    public String saludar() {
        return "Hola, Spring Boot está funcionando correctamente";
    }

    // Parte G: Endpoint de información (JSON)
    @GetMapping("/info")
    public ProyectoInfo obtenerInfo() {
        return new ProyectoInfo(
            "Desarrollo Web Integrado",
            "Semana 1",
            "Spring Boot + Maven + Java",
            "Entorno configurado correctamente"
        );
    }

    // Ejercicio 1: Endpoint de versión
    @GetMapping("/version")
    public String obtenerVersion() {
        return "Aplicación Semana 1 - Versión 1.0.0";
    }

    // Ejercicio 2: Endpoint de estado (JSON)
    @GetMapping("/estado")
    public EstadoInfo obtenerEstado() {
        return new EstadoInfo("semana1", true, "Backend disponible");
    }
}