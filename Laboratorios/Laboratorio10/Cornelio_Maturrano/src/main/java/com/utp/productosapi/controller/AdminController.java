
package com.utp.productosapi.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.productosapi.dto.ResumenBackendResponse;
import com.utp.productosapi.service.AdminResumenService;
import com.utp.productosapi.service.ProductoService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductoService productoService;
    private final AdminResumenService adminResumenService;

    public AdminController(
            ProductoService productoService,
            AdminResumenService adminResumenService) {

        this.productoService = productoService;
        this.adminResumenService = adminResumenService;
    }

    // REPORTE ADMINISTRATIVO - LABORATORIO 09
    @GetMapping("/reporte")
    public Map<String, Object> obtenerReporte() {

        long totalProductos = productoService.contarProductos();

        long productosStockBajo =
                productoService.contarProductosConStockBajo(5);

        return Map.of(
            "fechaServidor", LocalDateTime.now().toString(),
            "totalProductos", totalProductos,
            "productosStockBajo", productosStockBajo
        );
    }

    // RESUMEN ADMINISTRATIVO - LABORATORIO 10
    @GetMapping("/resumen")
    public ResumenBackendResponse resumen() {
        return adminResumenService.obtenerResumen();
    }
}
