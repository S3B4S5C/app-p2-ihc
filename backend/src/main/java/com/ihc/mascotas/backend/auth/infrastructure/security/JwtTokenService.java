package com.ihc.mascotas.backend.auth.infrastructure.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

import com.ihc.mascotas.backend.auth.application.TokenService;
import com.ihc.mascotas.backend.auth.domain.User;

@Component
public class JwtTokenService implements TokenService {

    private final JwtEncoder encoder;
    private final Duration ttl;
    private final String issuer;
    private final Clock clock;

    @Autowired
    public JwtTokenService(
            JwtEncoder encoder,
            @Value("${app.security.jwt.ttl:PT8H}") Duration ttl,
            @Value("${app.security.jwt.issuer:mascotas-al-dia}") String issuer) {
        this(encoder, ttl, issuer, Clock.systemUTC());
    }

    JwtTokenService(JwtEncoder encoder, Duration ttl, String issuer, Clock clock) {
        this.encoder = encoder;
        this.ttl = ttl;
        this.issuer = issuer;
        this.clock = clock;
    }

    @Override
    public IssuedToken issue(User user) {
        Instant issuedAt = Instant.now(clock);
        Instant expiresAt = issuedAt.plus(ttl);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.email())
                .claim("uid", user.id().toString())
                .claim("name", user.fullName())
                .claim("role", "USER")
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new IssuedToken(token, expiresAt);
    }
}
