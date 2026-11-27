
package com.utp.productosapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utp.productosapi.model.Producto;
import com.utp.productosapi.service.ProductoService;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final ProductoService service;

    public InventarioController(ProductoService service) {
        this.service = service;
    }

    // REGISTRAR SALIDA DE INVENTARIO - LABORATORIO 10
    @PostMapping("/{id}/salida")
    public ResponseEntity<Producto> registrarSalida(
            @PathVariable Long id,
            @RequestParam int cantidad) {

        return ResponseEntity.ok(
                service.registrarSalida(id, cantidad)
        );
    }
}

