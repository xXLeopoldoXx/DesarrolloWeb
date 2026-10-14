package com.utp.semana4_api_rest.controller;

import java.util.Map;

import jakarta.validation.Valid;

import com.utp.semana4_api_rest.dto.LoginRequest;
import com.utp.semana4_api_rest.dto.TokenResponse;
import com.utp.semana4_api_rest.security.TokenService;

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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokens;

    public AuthController(
            AuthenticationManager manager,
            TokenService tokens) {

        this.manager = manager;
        this.tokens = tokens;
    }

    // INICIAR SESION Y GENERAR TOKEN JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        try {

            // VALIDAR USUARIO Y CONTRASEÑA
            Authentication autenticacion = manager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(),
                            request.password()
                    )
            );

            // GENERAR TOKEN JWT
            String jwt = tokens.crearToken(autenticacion);

            // DEVOLVER TOKEN AL USUARIO
            return ResponseEntity.ok(
                    new TokenResponse(
                            "Bearer",
                            jwt,
                            tokens.expiresInSeconds()
                    )
            );

        } catch (AuthenticationException ex) {

            // CREDENCIALES INCORRECTAS
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Credenciales inválidas"
                    ));
        }
    }

    // CONSULTAR INFORMACION DEL USUARIO AUTENTICADO
    @GetMapping("/me")
    public Map<String, Object> miPerfil(
            JwtAuthenticationToken auth) {

        var roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(autoridad -> autoridad.startsWith("ROLE_"))
                .toList();

        return Map.of(
                "username", auth.getName(),
                "roles", roles
        );
    }
}
