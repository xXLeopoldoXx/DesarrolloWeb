package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ReaccionRequest;
import com.utp.proyectoriesgo.dto.ReaccionesResponse;
import com.utp.proyectoriesgo.service.ReaccionService;
import com.utp.proyectoriesgo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Reacciones de apoyo a una publicación (cada POST alterna: la pone o la quita). */
@RestController
@RequestMapping("/api/comunidad/publicaciones/{publicacionId}/reacciones")
public class ReaccionController {

    private final ReaccionService reacciones;
    private final UsuarioService usuarias;

    public ReaccionController(ReaccionService reacciones, UsuarioService usuarias) {
        this.reacciones = reacciones;
        this.usuarias = usuarias;
    }

    @PostMapping
    public ReaccionesResponse alternar(@PathVariable Long publicacionId,
                                       @Valid @RequestBody ReaccionRequest solicitud,
                                       Authentication autenticacion) {
        return reacciones.alternar(usuarias.actual(autenticacion).getId(), publicacionId, solicitud.tipo());
    }
}
