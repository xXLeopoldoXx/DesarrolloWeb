package com.utp.semana4_api_rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // ENDPOINT PUBLICO
                .requestMatchers(
                    HttpMethod.GET, "/api/publico/**")
                    .permitAll()

                // INVENTARIO - LAB 08
                // Solo ADMIN puede realizar ajustes
                .requestMatchers(
                    HttpMethod.POST, "/api/inventario/ajustes")
                    .hasRole("ADMIN")

                // USER y ADMIN pueden consultar inventario
                .requestMatchers(
                    HttpMethod.GET, "/api/inventario/**")
                    .hasAnyRole("USER", "ADMIN")

                // PRODUCTOS - LAB 06 Y 07
                // USER y ADMIN pueden consultar
                .requestMatchers(
                    HttpMethod.GET, "/api/productos/**")
                    .hasAnyRole("USER", "ADMIN")

                // Solo ADMIN puede crear
                .requestMatchers(
                    HttpMethod.POST, "/api/productos/**")
                    .hasRole("ADMIN")

                // Solo ADMIN puede actualizar
                .requestMatchers(
                    HttpMethod.PUT, "/api/productos/**")
                    .hasRole("ADMIN")

                // Solo ADMIN puede modificar stock y precio
                .requestMatchers(
                    HttpMethod.PATCH, "/api/productos/**")
                    .hasRole("ADMIN")

                // Solo ADMIN puede eliminar
                .requestMatchers(
                    HttpMethod.DELETE, "/api/productos/**")
                    .hasRole("ADMIN")

                // Otros endpoints requieren autenticacion
                .anyRequest().authenticated()
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
