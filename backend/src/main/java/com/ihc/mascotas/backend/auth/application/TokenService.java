package com.ihc.mascotas.backend.auth.application;

import java.time.Instant;

import com.ihc.mascotas.backend.auth.domain.User;

public interface TokenService {
    IssuedToken issue(User user);

    record IssuedToken(String value, Instant expiresAt) { }
}
