package com.ihc.mascotas.backend.auth.domain;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetToken(
        UUID id,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        Instant createdAt) {
}
