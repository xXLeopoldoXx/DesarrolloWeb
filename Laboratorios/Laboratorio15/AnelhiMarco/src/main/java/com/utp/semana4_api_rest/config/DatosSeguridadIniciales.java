package com.utp.semana4_api_rest.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utp.semana4_api_rest.model.Rol;
import com.utp.semana4_api_rest.model.Usuario;
import com.utp.semana4_api_rest.repository.RolRepository;
import com.utp.semana4_api_rest.repository.UsuarioRepository;

@Configuration
@Profile("demo")
public class DatosSeguridadIniciales {

    @Bean
    public CommandLineRunner cargarUsuarios(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${LAB15_DEMO_USER_PASSWORD}") String userPassword,
            @Value("${LAB15_DEMO_ADMIN_PASSWORD}") String adminPassword) {

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
                        passwordEncoder.encode(userPassword),
                        true);

                usuario.setRoles(Set.of(rolUser));

                usuarioRepository.save(usuario);
            }

            // CREAR USUARIO ADMINISTRADOR
            if (usuarioRepository.findByUsername("admin").isEmpty()) {

                Usuario admin = new Usuario(
                        "admin",
                        passwordEncoder.encode(adminPassword),
                        true);

                admin.setRoles(Set.of(rolAdmin, rolUser));

                usuarioRepository.save(admin);
            }
        };
    }
}
