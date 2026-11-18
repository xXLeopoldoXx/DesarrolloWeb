package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.PerfilResponse;
import com.utp.proyectoriesgo.model.Usuario;
import com.utp.proyectoriesgo.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mi")
public class MiPerfilController {

    private final UsuarioService usuarioService;

    public MiPerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public PerfilResponse perfil(Authentication autenticacion) {
        Usuario usuario = usuarioService.actual(autenticacion);
        var roles = autenticacion.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .toList();
        return new PerfilResponse(usuario.getId(), usuario.getUsername(), usuario.getSeudonimo(), roles);
    }
}
