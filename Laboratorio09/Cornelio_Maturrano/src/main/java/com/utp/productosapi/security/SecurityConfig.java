
package com.utp.productosapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService users,
            PasswordEncoder passwords) {

        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(passwords);

        return new ProviderManager(provider);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        var authorities = new JwtGrantedAuthoritiesConverter();

        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter converter)
            throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Login publico
                .requestMatchers(
                    HttpMethod.POST, "/api/auth/login"
                ).permitAll()

                // Endpoint publico del laboratorio 08
                .requestMatchers("/api/publico/**").permitAll()

                // Reporte exclusivo para administradores
                .requestMatchers(
                    HttpMethod.GET, "/api/admin/reporte"
                ).hasRole("ADMIN")

                // Consultas: USER y ADMIN
                .requestMatchers(
                    HttpMethod.GET, "/api/productos/**"
                ).hasAnyRole("USER", "ADMIN")

                // Registro: solo ADMIN
                .requestMatchers(
                    HttpMethod.POST, "/api/productos/**"
                ).hasRole("ADMIN")

                // Actualizacion: solo ADMIN
                .requestMatchers(
                    HttpMethod.PUT, "/api/productos/**"
                ).hasRole("ADMIN")

                // Actualizacion parcial: solo ADMIN
                .requestMatchers(
                    HttpMethod.PATCH, "/api/productos/**"
                ).hasRole("ADMIN")

                // Eliminacion: solo ADMIN
                .requestMatchers(
                    HttpMethod.DELETE, "/api/productos/**"
                ).hasRole("ADMIN")

                // Otros endpoints requieren autenticacion
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(converter)
                )
            );

        return http.build();
    }
}
