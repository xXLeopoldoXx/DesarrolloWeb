package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.AsistenteEstadoResponse;
import com.utp.proyectoriesgo.dto.AsistenteRequest;
import com.utp.proyectoriesgo.dto.AsistenteResponse;
import com.utp.proyectoriesgo.service.AsistenteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asistente")
public class AsistenteController {

    private final AsistenteService asistente;

    public AsistenteController(AsistenteService asistente) {
        this.asistente = asistente;
    }

    @PostMapping("/mensajes")
    public AsistenteResponse responder(@Valid @RequestBody AsistenteRequest request,
                                       Authentication autenticacion) {
        return asistente.responder(autenticacion.getName(), request);
    }

    /** Permite que el frontend avise si el asistente aún no está configurado. */
    @GetMapping("/estado")
    public AsistenteEstadoResponse estado() {
        return new AsistenteEstadoResponse(asistente.disponible());
    }
}
