package com.utp.proyectoriesgo.controller;

import com.utp.proyectoriesgo.dto.LoginRequest;
import com.utp.proyectoriesgo.dto.TokenResponse;
import com.utp.proyectoriesgo.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokens;

    public AuthController(AuthenticationManager manager, TokenService tokens) {
        this.manager = manager;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication autenticacion = manager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(), request.password()));

            String jwt = tokens.crearToken(autenticacion);
            return ResponseEntity.ok(new TokenResponse("Bearer", jwt, tokens.expiresInSeconds()));
        } catch (AuthenticationException ex) {
            // Mensaje genérico a propósito: no revelamos si falló el usuario o la clave
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    @GetMapping("/me")
    public Map<String, Object> miPerfil(JwtAuthenticationToken auth) {
        var roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .toList();
        return Map.of("username", auth.getName(), "roles", roles);
    }
}
