package com.ihc.mascotas.backend.auth.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ihc.mascotas.backend.auth.domain.PasswordResetToken;
import com.ihc.mascotas.backend.auth.domain.PasswordResetTokenRepository;
import com.ihc.mascotas.backend.auth.domain.User;
import com.ihc.mascotas.backend.auth.domain.UserRepository;
import com.ihc.mascotas.backend.shared.exception.InvalidCurrentPasswordException;
import com.ihc.mascotas.backend.shared.exception.InvalidResetTokenException;
import com.ihc.mascotas.backend.shared.exception.NotFoundException;

@Service
public class PasswordService {

    private final UserRepository users;
    private final PasswordResetTokenRepository resetTokens;
    private final PasswordEncoder passwordEncoder;
    private final Duration resetTtl;
    private final Clock clock;
    private final SecureRandom secureRandom;

    @Autowired
    public PasswordService(
            UserRepository users,
            PasswordResetTokenRepository resetTokens,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.password-reset.ttl:PT15M}") Duration resetTtl) {
        this(users, resetTokens, passwordEncoder, resetTtl, Clock.systemUTC(), new SecureRandom());
    }

    PasswordService(
            UserRepository users,
            PasswordResetTokenRepository resetTokens,
            PasswordEncoder passwordEncoder,
            Duration resetTtl,
            Clock clock,
            SecureRandom secureRandom) {
        this.users = users;
        this.resetTokens = resetTokens;
        this.passwordEncoder = passwordEncoder;
        this.resetTtl = resetTtl;
        this.clock = clock;
        this.secureRandom = secureRandom;
    }

    @Transactional
    public PasswordResetResult requestReset(String email) {
        User user = users.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new NotFoundException("No existe una cuenta con ese correo electrónico"));

        resetTokens.deleteByUserId(user.id());

        Instant now = Instant.now(clock);
        String rawToken = generateToken();
        PasswordResetToken token = new PasswordResetToken(
                UUID.randomUUID(), user.id(), hash(rawToken), now.plus(resetTtl), now);
        resetTokens.save(token);

        return new PasswordResetResult(rawToken, token.expiresAt());
    }

    @Transactional
    public void confirmReset(String rawToken, String newPassword) {
        PasswordResetToken token = resetTokens.findByTokenHash(hash(rawToken))
                .orElseThrow(InvalidResetTokenException::new);

        Instant now = Instant.now(clock);
        if (!token.expiresAt().isAfter(now)) {
            resetTokens.deleteById(token.id());
            throw new InvalidResetTokenException();
        }

        User user = users.findById(token.userId())
                .orElseThrow(InvalidResetTokenException::new);
        users.save(withPassword(user, newPassword, now));
        resetTokens.deleteById(token.id());
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = users.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (!passwordEncoder.matches(currentPassword, user.passwordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        users.save(withPassword(user, newPassword, Instant.now(clock)));
        resetTokens.deleteByUserId(user.id());
    }

    private User withPassword(User user, String rawPassword, Instant updatedAt) {
        return new User(
                user.id(), user.fullName(), user.email(), passwordEncoder.encode(rawPassword),
                user.createdAt(), updatedAt);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 no está disponible", exception);
        }
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public record PasswordResetResult(String resetToken, Instant expiresAt) { }
}
