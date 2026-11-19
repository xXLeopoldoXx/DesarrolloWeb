package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.ContactoRequest;
import com.utp.proyectoriesgo.dto.ContactoResponse;
import com.utp.proyectoriesgo.service.ContactoService;
import com.utp.proyectoriesgo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mi/contactos")
public class MiContactosController {
    private final ContactoService contactos;
    private final UsuarioService usuarias;

    public MiContactosController(ContactoService contactos, UsuarioService usuarias) {
        this.contactos = contactos;
        this.usuarias = usuarias;
    }

    @GetMapping
    public List<ContactoResponse> listar(Authentication autenticacion) {
        return contactos.listar(usuarias.actual(autenticacion).getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactoResponse crear(Authentication autenticacion, @Valid @RequestBody ContactoRequest solicitud) {
        return contactos.crear(usuarias.actual(autenticacion).getId(), solicitud);
    }

    @PutMapping("/{id}")
    public ContactoResponse actualizar(Authentication autenticacion, @PathVariable Long id,
                                      @Valid @RequestBody ContactoRequest solicitud) {
        return contactos.actualizar(usuarias.actual(autenticacion).getId(), id, solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(Authentication autenticacion, @PathVariable Long id) {
        contactos.eliminar(usuarias.actual(autenticacion).getId(), id);
    }
}
