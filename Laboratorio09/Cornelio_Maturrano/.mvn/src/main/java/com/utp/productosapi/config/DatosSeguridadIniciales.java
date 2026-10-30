package com.utp.productosapi.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utp.productosapi.model.Rol;
import com.utp.productosapi.model.Usuario;
import com.utp.productosapi.repository.RolRepository;
import com.utp.productosapi.repository.UsuarioRepository;

@Configuration
public class DatosSeguridadIniciales {

    @Bean
    CommandLineRunner cargarUsuarios(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // CREAR ROL USER
            Rol rolUser = rolRepository
                .findByNombre("ROLE_USER")
                .orElseGet(() ->
                    rolRepository.save(new Rol("ROLE_USER"))
                );

            // CREAR ROL ADMIN
            Rol rolAdmin = rolRepository
                .findByNombre("ROLE_ADMIN")
                .orElseGet(() ->
                    rolRepository.save(new Rol("ROLE_ADMIN"))
                );

            // CREAR USUARIO NORMAL
            if (usuarioRepository.findByUsername("usuario").isEmpty()) {

                Usuario usuario = new Usuario(
                    "usuario",
                    passwordEncoder.encode("Usuario123*")
                );

                usuario.setRoles(Set.of(rolUser));

                usuarioRepository.save(usuario);
            }

            // CREAR ADMINISTRADOR
            if (usuarioRepository.findByUsername("admin").isEmpty()) {

                Usuario admin = new Usuario(
                    "admin",
                    passwordEncoder.encode("Admin123*")
                );

                admin.setRoles(Set.of(rolAdmin, rolUser));

                usuarioRepository.save(admin);
            }
        };
    }
}