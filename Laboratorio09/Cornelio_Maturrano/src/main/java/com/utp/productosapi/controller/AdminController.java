
package com.utp.productosapi.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.productosapi.service.ProductoService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductoService productoService;

    public AdminController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/reporte")
    public Map<String, Object> obtenerReporte() {

        long totalProductos = productoService.contarProductos();

        return Map.of(
            "fechaServidor", LocalDateTime.now().toString(),
            "totalProductos", totalProductos
        );
    }
}

