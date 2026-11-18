package com.bd.hotelapp.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final Clock clock;
    private final Duration expiration;

    public JwtTokenService(JwtEncoder jwtEncoder, Clock clock,
                           @Value("${app.jwt.expiration}") Duration expiration) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
        this.expiration = expiration;
    }

    public String generateToken(UUID id, String role) {
        Instant now = Instant.now(clock);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("hotelApp")
                .subject(id.toString())
                .claim("role", role)
                .issuedAt(now)
                .expiresAt(now.plus(expiration))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public Duration getExpiration() {
        return expiration;
    }
}