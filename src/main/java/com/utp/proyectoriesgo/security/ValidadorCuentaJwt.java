package com.utp.proyectoriesgo.security;

import com.utp.proyectoriesgo.repository.UsuarioRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class ValidadorCuentaJwt implements OAuth2TokenValidator<Jwt> {
    private final UsuarioRepository usuarias;

    public ValidadorCuentaJwt(UsuarioRepository usuarias) {
        this.usuarias = usuarias;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String nombre = jwt.getSubject();
        boolean activa = nombre != null && IdentidadJwt.idDe(jwt)
                .filter(id -> usuarias.existsByIdAndUsernameAndActivoTrue(id, nombre))
                .isPresent();
        if (activa) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
    }
}
