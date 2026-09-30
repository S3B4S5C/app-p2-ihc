package com.ihc.mascotas.backend.auth.domain;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String fullName,
        String email,
        String passwordHash,
        Instant createdAt,
        Instant updatedAt) {
}
