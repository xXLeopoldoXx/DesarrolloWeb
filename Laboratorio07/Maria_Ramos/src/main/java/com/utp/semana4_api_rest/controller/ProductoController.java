package com.utp.semana4_api_rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.utp.semana4_api_rest.dto.ActualizarStockRequest;
import com.utp.semana4_api_rest.dto.ProductoRequest;
import com.utp.semana4_api_rest.model.Producto;
import com.utp.semana4_api_rest.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // LISTAR PRODUCTOS - LAB 06
    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @RequestParam(required = false) String categoria) {

        return ResponseEntity.ok(service.listar(categoria));
    }

    // BUSCAR POR NOMBRE CON JPQL - LAB 07
    // Acepta "texto" (Lab 07) y "nombre" (Lab 06).
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String nombre) {

        String busqueda = texto != null ? texto : nombre;

        return ResponseEntity.ok(
                service.buscarPorNombre(busqueda));
    }

    // BUSCAR POR CATEGORIA - LAB 07
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> buscarPorCategoria(
            @PathVariable String categoria) {

        return ResponseEntity.ok(
                service.buscarPorCategoria(categoria));
    }

    // BUSCAR POR RANGO DE PRECIOS - LAB 07
    @GetMapping("/precio")
    public ResponseEntity<List<Producto>> buscarPorRango(
            @RequestParam double min,
            @RequestParam double max) {

        return ResponseEntity.ok(
                service.buscarPorRango(min, max));
    }

    // BUSCAR PRODUCTOS CON STOCK BAJO - LAB 07
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> buscarConStockBajo(
            @RequestParam Integer limite) {

        return ResponseEntity.ok(
                service.buscarConStockBajo(limite));
    }

    // BUSCAR PRODUCTO POR ID - LAB 06
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id));
    }

    // CREAR PRODUCTO - LAB 06
    @PostMapping
    public ResponseEntity<Producto> crear(
            @Valid @RequestBody ProductoRequest request) {

        Producto nuevoProducto = service.crear(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuevoProducto.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(nuevoProducto);
    }

    // ACTUALIZAR PRODUCTO - LAB 06
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {

        return ResponseEntity.ok(
                service.actualizar(id, request));
    }

    // ACTUALIZAR STOCK - LAB 06
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarStockRequest request) {

        return ResponseEntity.ok(
                service.actualizarStock(id, request));
    }

    // ACTUALIZAR PRECIO - LAB 06
    @PatchMapping("/{id}/precio")
    public ResponseEntity<Producto> actualizarPrecio(
            @PathVariable Long id,
            @RequestParam double valor) {

        return ResponseEntity.ok(
                service.actualizarPrecio(id, valor));
    }

    // REGISTRAR SALIDA DE INVENTARIO - LAB 07
    @PostMapping("/{id}/salidas")
    public ResponseEntity<Producto> registrarSalida(
            @PathVariable Long id,
            @RequestParam int cantidad) {

        return ResponseEntity.ok(
                service.registrarSalida(id, cantidad));
    }

    // SIMULAR ERROR PARA COMPROBAR ROLLBACK - LAB 07
    @PostMapping("/{id}/salidas/simular-error")
    public ResponseEntity<Void> simularError(
            @PathVariable Long id,
            @RequestParam int cantidad) {

        service.simularSalidaConError(id, cantidad);

        return ResponseEntity.noContent().build();
    }

    // REGISTRAR ENTRADA DE INVENTARIO - LAB 07
    @PostMapping("/{id}/entradas")
    public ResponseEntity<Producto> registrarEntrada(
            @PathVariable Long id,
            @RequestParam int cantidad) {

        return ResponseEntity.ok(
                service.registrarEntrada(id, cantidad));
    }

    // ELIMINAR PRODUCTO - LAB 06
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}
