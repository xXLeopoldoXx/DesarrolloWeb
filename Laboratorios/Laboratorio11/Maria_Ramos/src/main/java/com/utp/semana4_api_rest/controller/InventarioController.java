package com.utp.semana4_api_rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.service.ProductoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final ProductoService productoService;

    public InventarioController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // CONSULTAR INVENTARIO - USER Y ADMIN
    @GetMapping
    public ResponseEntity<List<Producto>> listarInventario() {

        return ResponseEntity.ok(
                productoService.listar(null));
    }

    // REALIZAR AJUSTES DE INVENTARIO - SOLO ADMIN
    @PostMapping("/ajustes")
    public ResponseEntity<Producto> ajustarInventario(
            @Valid @RequestBody AjusteInventarioRequest request) {

        Producto producto;

        if ("ENTRADA".equals(request.tipo())) {

            producto = productoService.registrarEntrada(
                    request.productoId(),
                    request.cantidad());

        } else {

            producto = productoService.registrarSalida(
                    request.productoId(),
                    request.cantidad());
        }

        return ResponseEntity.ok(producto);
    }

    // DATOS NECESARIOS PARA REALIZAR UN AJUSTE
    public record AjusteInventarioRequest(

            @NotNull
            Long productoId,

            @NotNull
            @Pattern(regexp = "ENTRADA|SALIDA",
                    message = "El tipo debe ser ENTRADA o SALIDA")
            String tipo,

            @Min(value = 1,
                    message = "La cantidad debe ser mayor que cero")
            int cantidad
    ) {
    }
}
