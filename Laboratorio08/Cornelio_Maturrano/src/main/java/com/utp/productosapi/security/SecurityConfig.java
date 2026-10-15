
package com.utp.productosapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
            .authorizeHttpRequests(auth -> auth

                // Endpoint publico
                .requestMatchers("/api/publico/**").permitAll()

                // Consultas: USER y ADMIN
                .requestMatchers(
                    HttpMethod.GET, "/api/productos/**"
                ).hasAnyRole("USER", "ADMIN")

                // Modificaciones: solo ADMIN
                .requestMatchers(
                    "/api/productos/**"
                ).hasRole("ADMIN")

                // Otros endpoints requieren autenticacion
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
