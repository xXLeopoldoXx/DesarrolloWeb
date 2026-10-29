package com.utp.semana4_api_rest.config;

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

    // CIFRAR Y VERIFICAR CONTRASEÑAS
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AUTENTICAR USUARIOS REGISTRADOS EN LA BASE DE DATOS
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService users,
            PasswordEncoder passwords) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(users);

        provider.setPasswordEncoder(passwords);

        return new ProviderManager(provider);
    }

    // CONVERTIR LOS ROLES DEL JWT EN PERMISOS DE SPRING SECURITY
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authorities =
                new JwtGrantedAuthoritiesConverter();

        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(authorities);

        return converter;
    }

    // CONFIGURAR LA SEGURIDAD DE LOS ENDPOINTS
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter converter) throws Exception {

        http
                // DESACTIVAR CSRF, HTTP BASIC Y FORMULARIO DE LOGIN
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // API SIN SESIONES
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // CONFIGURAR PERMISOS
                .authorizeHttpRequests(auth -> auth

                        // LOGIN PUBLICO
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login")
                        .permitAll()

                        // ENDPOINTS PUBLICOS
                        .requestMatchers("/api/publico/**")
                        .permitAll()

                        // REPORTES EXCLUSIVOS DEL ADMINISTRADOR
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // AJUSTES DE INVENTARIO: SOLO ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inventario/ajustes")
                        .hasRole("ADMIN")

                        // CONSULTAR INVENTARIO: USER Y ADMIN
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inventario/**")
                        .hasAnyRole("USER", "ADMIN")

                        // CONSULTAR PRODUCTOS: USER Y ADMIN
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/productos/**")
                        .hasAnyRole("USER", "ADMIN")

                        // CREAR PRODUCTOS: SOLO ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/productos/**")
                        .hasRole("ADMIN")

                        // ACTUALIZAR PRODUCTOS: SOLO ADMIN
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/productos/**")
                        .hasRole("ADMIN")

                        // MODIFICAR STOCK Y PRECIO: SOLO ADMIN
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/productos/**")
                        .hasRole("ADMIN")

                        // ELIMINAR PRODUCTOS: SOLO ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/productos/**")
                        .hasRole("ADMIN")

                        // LOS DEMAS ENDPOINTS REQUIEREN AUTENTICACION
                        .anyRequest()
                        .authenticated()
                )

                // HABILITAR AUTENTICACION MEDIANTE JWT
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(converter))
                );

        return http.build();
    }
}
