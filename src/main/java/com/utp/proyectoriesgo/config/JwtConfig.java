package com.utp.proyectoriesgo.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    // Convierte la clave Base64 (variable de entorno) en una clave HMAC
    @Bean
    SecretKey jwtSecretKey(@Value("${app.jwt.secret-base64}") String value) {
        byte[] raw = Base64.getDecoder().decode(value);
        if (raw.length < 32) {
            throw new IllegalArgumentException("JWT: se requieren al menos 32 bytes");
        }
        return new SecretKeySpec(raw, "HmacSHA256");
    }

    // FIRMA los tokens (lo usa TokenService al hacer login)
    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    // VERIFICA los tokens que llegan (firma, expiración y emisor)
    @Bean
    JwtDecoder jwtDecoder(SecretKey key, @Value("${app.jwt.issuer}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }
}