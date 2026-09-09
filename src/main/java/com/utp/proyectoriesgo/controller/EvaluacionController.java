package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ActualizarFactoresRequest;
import com.utp.proyectoriesgo.dto.EvaluacionRequest;
import com.utp.proyectoriesgo.model.Evaluacion;
import com.utp.proyectoriesgo.service.EvaluacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    private final EvaluacionService service;

    public EvaluacionController(EvaluacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Evaluacion>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evaluacion> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Evaluacion>> buscarPorTexto(@RequestParam String texto) {
        return ResponseEntity.ok(service.buscarPorTexto(texto));
    }

    @PostMapping
    public ResponseEntity<Evaluacion> crear(@RequestBody EvaluacionRequest request) {
        Evaluacion creada = service.crear(request);
        URI location = URI.create("/api/evaluaciones/" + creada.getId());
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evaluacion> actualizar(
            @PathVariable Long id, @RequestBody EvaluacionRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/factores")
    public ResponseEntity<Evaluacion> actualizarFactores(
            @PathVariable Long id, @RequestBody ActualizarFactoresRequest request) {
        return ResponseEntity.ok(service.actualizarFactores(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}