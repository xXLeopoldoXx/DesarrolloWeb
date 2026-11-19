package com.utp.tienda.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Generación y Validación de JWT - Deliz Astuhuaman")
class TokenServiceDelizTest {

    private TokenService tokenService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        byte[] rawKey = "deliz_secret_key_32_bytes_test_01".getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(rawKey, "HmacSHA256");

        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        tokenService = new TokenService(encoder, "tienda-api", 30);
    }

    @Test
    @DisplayName("01. Debe emitir un token JWT con subject 'deliz_astuhuaman' y sus roles correspondientes")
    void test01_debeEmitirTokenConUsuarioDelizYRolesCorrectos() {
        var autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                "deliz_astuhuaman",
                null,
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );

        String token = tokenService.crearToken(autenticacion);
        assertNotNull(token);

        Jwt decodificado = jwtDecoder.decode(token);
        assertEquals("deliz_astuhuaman", decodificado.getSubject());
        assertEquals("tienda-api", decodificado.getIssuer().toString());

        List<String> rolesEnClaim = decodificado.getClaimAsStringList("roles");
        assertNotNull(rolesEnClaim);
        assertTrue(rolesEnClaim.contains("ROLE_ADMIN"));
        assertTrue(rolesEnClaim.contains("ROLE_USER"));
        assertNotNull(decodificado.getExpiresAt());
    }

    @Test
    @DisplayName("02. Debe rechazar la decodificación si la firma del token fue manipulada")
    void test02_debeRechazarTokenSiLaFirmaFueModificada() {
        var autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                "deliz_astuhuaman",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String tokenValido = tokenService.crearToken(autenticacion);
        String[] partes = tokenValido.split("\\.");
        assertEquals(3, partes.length);

        // Alteramos el último carácter de la firma
        String firmaAlterada = partes[2].substring(0, partes[2].length() - 1) + "X";
        String tokenInvalido = partes[0] + "." + partes[1] + "." + firmaAlterada;

        assertThrows(BadJwtException.class, () -> jwtDecoder.decode(tokenInvalido));
    }

    @Test
    @DisplayName("03. Debe calcular el tiempo de vida en segundos adecuadamente (1800s)")
    void test03_debeCalcularExpiracionExactaEnSegundos() {
        assertEquals(1800, tokenService.expiresInSeconds());
    }

    @Test
    @DisplayName("04. Debe filtrar autoridades internas que no inicien con el prefijo ROLE_")
    void test04_debeFiltrarAutoridadesQueNoSeanRoles() {
        var autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                "deliz_operador",
                null,
                List.of(
                        new SimpleGrantedAuthority("PERMISO_VER_REPORTES"),
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );

        String token = tokenService.crearToken(autenticacion);
        Jwt decodificado = jwtDecoder.decode(token);

        List<String> roles = decodificado.getClaimAsStringList("roles");
        assertEquals(1, roles.size());
        assertEquals("ROLE_USER", roles.get(0));
    }
}
