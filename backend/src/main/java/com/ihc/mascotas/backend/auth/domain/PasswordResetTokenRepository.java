package com.ihc.mascotas.backend.auth.domain;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    PasswordResetToken save(PasswordResetToken token);
    void deleteByUserId(UUID userId);
    void deleteById(UUID id);
}
