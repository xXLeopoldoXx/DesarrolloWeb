package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ActualizarFactoresRequest;
import com.utp.proyectoriesgo.dto.EvaluacionRequest;
import com.utp.proyectoriesgo.model.Evaluacion;
import com.utp.proyectoriesgo.service.EvaluacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.utp.proyectoriesgo.dto.SeguimientoRequest;
import com.utp.proyectoriesgo.dto.SeguimientoResponse;
import com.utp.proyectoriesgo.model.Seguimiento;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;


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
    public ResponseEntity<Evaluacion> crear(
            @Valid @RequestBody EvaluacionRequest request) {

        Evaluacion creada = service.crear(request);
        URI location = URI.create("/api/evaluaciones/" + creada.getId());

        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evaluacion> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EvaluacionRequest request) {

        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/factores")
    public ResponseEntity<Evaluacion> actualizarFactores(
            @PathVariable Long id,
            @RequestBody ActualizarFactoresRequest request) {

        return ResponseEntity.ok(service.actualizarFactores(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<Evaluacion>> buscarPorNivel(@PathVariable String nivel) {
        return ResponseEntity.ok(service.buscarPorNivelRiesgo(nivel));
    }

    @GetMapping("/rango-fecha")
    public ResponseEntity<List<Evaluacion>> buscarPorRangoFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(service.buscarPorRangoFecha(desde, hasta));
    }

    @PostMapping("/{id}/seguimientos")
    public ResponseEntity<SeguimientoResponse> registrarSeguimiento(
            @PathVariable Long id, @Valid @RequestBody SeguimientoRequest request) {
        Seguimiento s = service.registrarSeguimiento(id, request.nota());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SeguimientoResponse(s.getId(), id, s.getNota(), s.getFecha()));
    }

    @GetMapping("/{id}/seguimientos")
    public ResponseEntity<List<SeguimientoResponse>> listarSeguimientos(@PathVariable Long id) {
        List<SeguimientoResponse> body = service.listarSeguimientos(id).stream()
                .map(s -> new SeguimientoResponse(s.getId(), id, s.getNota(), s.getFecha()))
                .toList();
        return ResponseEntity.ok(body);
    }

    @PostMapping("/{id}/seguimientos/simular-error")
    public ResponseEntity<Void> simularErrorSeguimiento(
            @PathVariable Long id, @Valid @RequestBody SeguimientoRequest request) {
        service.simularErrorSeguimiento(id, request.nota());
        return ResponseEntity.noContent().build();
    }
}