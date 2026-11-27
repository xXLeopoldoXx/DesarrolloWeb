package com.utp.semana4_api_rest.security;

import com.utp.semana4_api_rest.model.Usuario;
import com.utp.semana4_api_rest.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Buscar el usuario en PostgreSQL
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"));

        // Obtener los roles asignados al usuario
        String[] autoridades = usuario.getRoles()
                .stream()
                .map(rol -> rol.getNombre())
                .toArray(String[]::new);

        // Convertir el usuario a UserDetails
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(autoridades)
                .disabled(!usuario.isActivo())
                .build();
    }
}
