package com.utp.proyectoriesgo.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenServiceTest {

    private final SecretKey key = new SecretKeySpec(
            "0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    private final JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
    private final JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS256).build();

    @Test
    void tokenContieneUsuarioYRolFirmados() {
        TokenService service = new TokenService(encoder, "riesgo-api", 30);
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String token = service.crearToken(auth);
        Jwt decoded = decoder.decode(token);

        assertEquals("admin", decoded.getSubject());
        assertEquals("riesgo-api", decoded.getClaimAsString("iss"));
        assertTrue(decoded.getClaimAsStringList("roles").contains("ROLE_ADMIN"));
        assertNotNull(decoded.getExpiresAt());
    }

    @Test
    void expiresInSeconds_convierteMinutosASegundos() {
        TokenService service = new TokenService(encoder, "riesgo-api", 30);
        assertEquals(1800, service.expiresInSeconds());
    }
}