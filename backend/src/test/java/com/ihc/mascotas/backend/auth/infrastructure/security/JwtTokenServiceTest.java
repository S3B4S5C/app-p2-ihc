package com.ihc.mascotas.backend.auth.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import com.ihc.mascotas.backend.auth.application.TokenService;
import com.ihc.mascotas.backend.auth.domain.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtTokenServiceTest {

    @Test
    void issuedJwtContainsExpectedIdentityAndExpiration() {
        byte[] bytes = "test-secret-that-has-at-least-32-bytes-long".getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(bytes, "HmacSHA256");
        NimbusJwtEncoder encoder = NimbusJwtEncoder.withSecretKey(key)
                .algorithm(MacAlgorithm.HS256)
                .build();
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-30T12:00:00Z"),
                ZoneOffset.UTC);

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        JwtTimestampValidator timestampValidator = new JwtTimestampValidator();

        timestampValidator.setClock(clock);

        decoder.setJwtValidator(timestampValidator);
        JwtTokenService service = new JwtTokenService(encoder, Duration.ofHours(8), "mascotas-al-dia", clock);
        User user = new User(UUID.fromString("db490275-6bd7-433b-8564-7291a92d0af3"),
                "Ana Pérez", "ana@example.com", "hash", clock.instant(), clock.instant());

        TokenService.IssuedToken token = service.issue(user);
        Jwt jwt = decoder.decode(token.value());

        assertEquals("ana@example.com", jwt.getSubject());
        assertEquals(user.id().toString(), jwt.getClaimAsString("uid"));
        assertEquals("Ana Pérez", jwt.getClaimAsString("name"));
        assertEquals("USER", jwt.getClaimAsString("role"));
        assertEquals(Instant.parse("2026-09-30T20:00:00Z"), token.expiresAt());
    }
}
