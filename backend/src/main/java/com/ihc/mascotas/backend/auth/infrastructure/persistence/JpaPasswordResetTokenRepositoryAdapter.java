package com.ihc.mascotas.backend.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.ihc.mascotas.backend.auth.domain.PasswordResetToken;
import com.ihc.mascotas.backend.auth.domain.PasswordResetTokenRepository;

@Repository
class JpaPasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final SpringDataPasswordResetTokenRepository repository;

    JpaPasswordResetTokenRepositoryAdapter(SpringDataPasswordResetTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(JpaPasswordResetTokenRepositoryAdapter::toDomain);
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        var entity = new PasswordResetTokenJpaEntity(
                token.id(), token.userId(), token.tokenHash(), token.expiresAt(), token.createdAt());
        return toDomain(repository.save(entity));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(userId);
        repository.flush();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private static PasswordResetToken toDomain(PasswordResetTokenJpaEntity entity) {
        return new PasswordResetToken(entity.id, entity.userId, entity.tokenHash, entity.expiresAt, entity.createdAt);
    }
}
