package com.utp.semana4_api_rest.security;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class TokenServiceTest {

    @Test
    void debeGenerarTokenJwtValido() {

        // CLAVE DE PRUEBA DE 32 BYTES
        byte[] clave = new byte[32];

        for (int i = 0; i < clave.length; i++) {
            clave[i] = (byte) (i + 1);
        }

        SecretKey secretKey =
                new SecretKeySpec(clave, "HmacSHA256");

        // CONFIGURAR GENERADOR JWT
        JwtEncoder encoder =
                new NimbusJwtEncoder(
                        new ImmutableSecret<>(secretKey));

        // CONFIGURAR VALIDADOR JWT
        JwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        // CREAR SERVICIO DE TOKENS
        TokenService tokenService =
                new TokenService(encoder, "tienda-api", 30);

        // SIMULAR USUARIO ADMIN AUTENTICADO
        Authentication autenticacion =
                UsernamePasswordAuthenticationToken.authenticated(
                        "admin",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        // GENERAR TOKEN
        Instant antes = Instant.now();

        String token = tokenService.crearToken(autenticacion);

        Instant despues = Instant.now();

        // DECODIFICAR Y VALIDAR TOKEN
        Jwt jwt = decoder.decode(token);

        // VERIFICAR RESULTADOS
        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals("admin", jwt.getSubject());
        assertEquals("tienda-api", jwt.getIssuer().toString());

        assertTrue(
                jwt.getClaimAsStringList("roles")
                        .contains("ROLE_ADMIN")
        );

        assertNotNull(jwt.getIssuedAt());
        assertNotNull(jwt.getExpiresAt());

        assertFalse(jwt.getIssuedAt().isBefore(antes));
        assertFalse(jwt.getIssuedAt().isAfter(despues));

        assertEquals(
                1800,
                jwt.getExpiresAt().getEpochSecond()
                        - jwt.getIssuedAt().getEpochSecond()
        );

        assertEquals(1800, tokenService.expiresInSeconds());
    }
}
