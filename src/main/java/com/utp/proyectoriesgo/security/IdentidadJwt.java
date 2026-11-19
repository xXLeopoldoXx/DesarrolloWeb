package com.utp.proyectoriesgo.security;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public final class IdentidadJwt {
    static final String ID_CUENTA = "uid";

    private IdentidadJwt() {
    }

    public static Optional<Long> idDe(Jwt jwt) {
        if (!(jwt.getClaims().get(ID_CUENTA) instanceof String valor)) {
            return Optional.empty();
        }
        try {
            long id = Long.parseLong(valor);
            return id > 0 ? Optional.of(id) : Optional.empty();
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}
