package com.utp.proyectoriesgo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

final class UsuariaAutenticada extends User {
    private final Long id;

    UsuariaAutenticada(Long id, String username, String password, boolean activa,
                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, activa, true, true, true, authorities);
        this.id = id;
    }

    Long id() {
        return id;
    }
}
