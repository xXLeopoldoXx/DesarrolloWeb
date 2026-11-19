package com.utp.semana4_api_rest.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.semana4_api_rest.service.ProductoService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductoService productoService;

    public AdminController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // LABORATORIO 09 - REPORTE ADMINISTRATIVO
    @GetMapping("/reporte")
    public Map<String, Object> obtenerReporte() {

        long totalProductos = productoService.contarProductos();

        return Map.of(
                "fechaServidor", LocalDateTime.now().toString(),
                "totalProductos", totalProductos
        );
    }
}
