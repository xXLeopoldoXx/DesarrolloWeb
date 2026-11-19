package com.utp.tienda.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utp.tienda.model.Producto;
import com.utp.tienda.service.ProductoService;

@RestController
@RequestMapping("/api")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/productos")
    public List<Producto> listar() {
        return service.listar();
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(producto));
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(service.actualizar(id, producto));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Reto de aplicación (Sección 15.1 de la Guía 09)
    @GetMapping("/admin/reporte")
    public Map<String, Object> reporteAdmin() {
        return Map.of(
                "fechaServidor", LocalDateTime.now().toString(),
                "totalProductos", service.contarTotal(),
                "estado", "OK"
        );
    }
}
