package com.utp.semana4_api_rest.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final String issuer;
    private final long ttlMinutes;

    public TokenService(
            JwtEncoder encoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.ttl-minutes:30}") long ttlMinutes) {

        this.encoder = encoder;
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    // OBTENER LA DURACION DEL TOKEN EN SEGUNDOS
    public long expiresInSeconds() {
        return ttlMinutes * 60;
    }

    // GENERAR TOKEN JWT
    public String crearToken(Authentication autenticacion) {

        Instant ahora = Instant.now();

        // OBTENER LOS ROLES DEL USUARIO
        var roles = autenticacion.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(autoridad -> autoridad.startsWith("ROLE_"))
                .toList();

        // DEFINIR LOS DATOS DEL TOKEN
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(autenticacion.getName())
                .issuedAt(ahora)
                .expiresAt(
                        ahora.plus(ttlMinutes, ChronoUnit.MINUTES))
                .claim("roles", roles)
                .build();

        // FIRMAR EL TOKEN CON HS256
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
