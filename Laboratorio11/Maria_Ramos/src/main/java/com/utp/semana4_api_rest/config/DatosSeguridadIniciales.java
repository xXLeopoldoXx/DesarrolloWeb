package com.utp.semana4_api_rest.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utp.semana4_api_rest.model.Rol;
import com.utp.semana4_api_rest.model.Usuario;
import com.utp.semana4_api_rest.repository.RolRepository;
import com.utp.semana4_api_rest.repository.UsuarioRepository;

@Configuration
public class DatosSeguridadIniciales {

    @Bean
    public CommandLineRunner cargarUsuarios(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // CREAR ROL USER SI NO EXISTE
            Rol rolUser = rolRepository.findByNombre("ROLE_USER")
                    .orElseGet(() ->
                            rolRepository.save(
                                    new Rol("ROLE_USER")));

            // CREAR ROL ADMIN SI NO EXISTE
            Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseGet(() ->
                            rolRepository.save(
                                    new Rol("ROLE_ADMIN")));

            // CREAR USUARIO NORMAL
            if (usuarioRepository.findByUsername("usuario").isEmpty()) {

                Usuario usuario = new Usuario(
                        "usuario",
                        passwordEncoder.encode("Usuario123*"),
                        true);

                usuario.setRoles(Set.of(rolUser));

                usuarioRepository.save(usuario);
            }

            // CREAR USUARIO ADMINISTRADOR
            if (usuarioRepository.findByUsername("admin").isEmpty()) {

                Usuario admin = new Usuario(
                        "admin",
                        passwordEncoder.encode("Admin123*"),
                        true);

                admin.setRoles(Set.of(rolAdmin, rolUser));

                usuarioRepository.save(admin);
            }
        };
    }
}
