package com.ihc.mascotas.backend.auth.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
}
