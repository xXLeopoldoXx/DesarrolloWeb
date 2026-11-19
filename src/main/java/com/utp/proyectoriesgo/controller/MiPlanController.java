package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.AccionEstadoRequest;
import com.utp.proyectoriesgo.dto.AccionRequest;
import com.utp.proyectoriesgo.dto.AccionResponse;
import com.utp.proyectoriesgo.dto.LugarRequest;
import com.utp.proyectoriesgo.dto.LugarResponse;
import com.utp.proyectoriesgo.service.PlanSeguridadService;
import com.utp.proyectoriesgo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mi/plan")
public class MiPlanController {
    private final PlanSeguridadService plan;
    private final UsuarioService usuarias;

    public MiPlanController(PlanSeguridadService plan, UsuarioService usuarias) {
        this.plan = plan;
        this.usuarias = usuarias;
    }

    @GetMapping("/acciones")
    public List<AccionResponse> listarAcciones(Authentication autenticacion) {
        return plan.listarAcciones(usuarias.actual(autenticacion).getId());
    }

    @PostMapping("/acciones")
    @ResponseStatus(HttpStatus.CREATED)
    public AccionResponse crearAccion(Authentication autenticacion, @Valid @RequestBody AccionRequest solicitud) {
        return plan.crearAccion(usuarias.actual(autenticacion).getId(), solicitud);
    }

    @PatchMapping("/acciones/{id}")
    public AccionResponse marcarAccion(Authentication autenticacion, @PathVariable Long id,
                                      @Valid @RequestBody AccionEstadoRequest solicitud) {
        return plan.marcarAccion(usuarias.actual(autenticacion).getId(), id, solicitud.completada());
    }

    @DeleteMapping("/acciones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarAccion(Authentication autenticacion, @PathVariable Long id) {
        plan.eliminarAccion(usuarias.actual(autenticacion).getId(), id);
    }

    @GetMapping("/lugares")
    public List<LugarResponse> listarLugares(Authentication autenticacion) {
        return plan.listarLugares(usuarias.actual(autenticacion).getId());
    }

    @PostMapping("/lugares")
    @ResponseStatus(HttpStatus.CREATED)
    public LugarResponse crearLugar(Authentication autenticacion, @Valid @RequestBody LugarRequest solicitud) {
        return plan.crearLugar(usuarias.actual(autenticacion).getId(), solicitud);
    }

    @DeleteMapping("/lugares/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarLugar(Authentication autenticacion, @PathVariable Long id) {
        plan.eliminarLugar(usuarias.actual(autenticacion).getId(), id);
    }
}
