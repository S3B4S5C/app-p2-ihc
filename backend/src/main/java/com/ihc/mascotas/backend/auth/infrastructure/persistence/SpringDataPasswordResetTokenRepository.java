package com.ihc.mascotas.backend.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {
    Optional<PasswordResetTokenJpaEntity> findByTokenHash(String tokenHash);
    void deleteByUserId(UUID userId);
}
