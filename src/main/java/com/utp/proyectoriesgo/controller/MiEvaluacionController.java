package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.AutoevaluacionRequest;
import com.utp.proyectoriesgo.dto.ResultadoResponse;
import com.utp.proyectoriesgo.dto.ResumenEvaluacionResponse;
import com.utp.proyectoriesgo.service.AutoevaluacionService;
import com.utp.proyectoriesgo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** «Mi evaluación»: la autoevaluación de la usuaria autenticada (nunca la de otra). */
@RestController
@RequestMapping("/api/mi/evaluaciones")
public class MiEvaluacionController {
    private final AutoevaluacionService evaluaciones;
    private final UsuarioService usuarias;

    public MiEvaluacionController(AutoevaluacionService evaluaciones, UsuarioService usuarias) {
        this.evaluaciones = evaluaciones;
        this.usuarias = usuarias;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultadoResponse crear(Authentication autenticacion, @Valid @RequestBody AutoevaluacionRequest solicitud) {
        return evaluaciones.crear(usuarias.actual(autenticacion).getId(), solicitud);
    }

    @GetMapping
    public List<ResumenEvaluacionResponse> listar(Authentication autenticacion) {
        return evaluaciones.listar(usuarias.actual(autenticacion).getId());
    }

    @GetMapping("/{id}")
    public ResultadoResponse detalle(Authentication autenticacion, @PathVariable Long id) {
        return evaluaciones.detalle(usuarias.actual(autenticacion).getId(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(Authentication autenticacion, @PathVariable Long id) {
        evaluaciones.eliminar(usuarias.actual(autenticacion).getId(), id);
    }
}
