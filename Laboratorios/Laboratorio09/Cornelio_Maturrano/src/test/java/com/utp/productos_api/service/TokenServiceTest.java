
package com.utp.productosapi.security;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

class TokenServiceTest {

    @Test
    void debeGenerarTokenJwtValido() {

        byte[] clave = new byte[32];

        for (int i = 0; i < clave.length; i++) {
            clave[i] = (byte) (i + 1);
        }

        SecretKey secretKey =
                new SecretKeySpec(clave, "HmacSHA256");

        JwtEncoder encoder =
                new NimbusJwtEncoder(
                    new ImmutableSecret<>(secretKey)
                );

        JwtDecoder decoder =
                NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();

        TokenService service =
                new TokenService(encoder, "tienda-api", 30);

        var autenticacion =
                UsernamePasswordAuthenticationToken.authenticated(
                    "admin",
                    null,
                    List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                    )
                );

        String token = service.crearToken(autenticacion);

        Jwt jwt = decoder.decode(token);

        assertNotNull(token);
        assertEquals("admin", jwt.getSubject());
        assertEquals("tienda-api", jwt.getClaimAsString("iss"));

        assertTrue(
            jwt.getClaimAsStringList("roles")
                .contains("ROLE_ADMIN")
        );

        assertEquals(1800, service.expiresInSeconds());

        assertEquals(
            30,
            Duration.between(
                jwt.getIssuedAt(),
                jwt.getExpiresAt()
            ).toMinutes()
        );
    }
}
