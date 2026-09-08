package com.utp.proyectoriesgo.controller;

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
    public List<Evaluacion> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Evaluacion buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Evaluacion> crear(@RequestBody EvaluacionRequest request) {
        Evaluacion creada = service.crear(request);
        URI location = URI.create("/api/evaluaciones/" + creada.getId());
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public Evaluacion actualizar(@PathVariable Long id, @RequestBody EvaluacionRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
