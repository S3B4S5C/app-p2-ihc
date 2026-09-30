package com.ihc.mascotas.backend.auth.application;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ihc.mascotas.backend.auth.domain.PasswordResetToken;
import com.ihc.mascotas.backend.auth.domain.PasswordResetTokenRepository;
import com.ihc.mascotas.backend.auth.domain.User;
import com.ihc.mascotas.backend.auth.domain.UserRepository;
import com.ihc.mascotas.backend.shared.exception.InvalidCurrentPasswordException;
import com.ihc.mascotas.backend.shared.exception.InvalidResetTokenException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final PasswordResetTokenRepository resetTokens = mock(PasswordResetTokenRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final SecureRandom secureRandom = new SecureRandom(new byte[] { 1, 2, 3, 4 });
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-30T12:00:00Z"), ZoneOffset.UTC);
    private PasswordService service;

    @BeforeEach
    void setUp() {
        reset(users, resetTokens, encoder);
        service = new PasswordService(users, resetTokens, encoder, Duration.ofMinutes(15), clock, secureRandom);
    }

    @Test
    void requestResetCreatesTemporaryTokenForExistingAccount() {
        User user = user();
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(resetTokens.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordService.PasswordResetResult result = service.requestReset(" ANA@example.com ");

        assertNotNull(result.resetToken());
        assertTrue(result.resetToken().length() >= 40);
        assertEquals(Instant.parse("2026-09-30T12:15:00Z"), result.expiresAt());
        verify(resetTokens).deleteByUserId(user.id());
        verify(resetTokens).save(argThat(token -> token.userId().equals(user.id())
                && token.tokenHash().length() == 64));
    }

    @Test
    void confirmResetChangesPasswordAndConsumesToken() {
        User user = user();
        PasswordService.PasswordResetResult issued = issueFor(user);
        PasswordResetToken stored = captureSavedToken();
        when(resetTokens.findByTokenHash(stored.tokenHash())).thenReturn(Optional.of(stored));
        when(users.findById(user.id())).thenReturn(Optional.of(user));
        when(encoder.encode("new-password")).thenReturn("new-hash");
        when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.confirmReset(issued.resetToken(), "new-password");

        verify(users).save(argThat(saved -> saved.passwordHash().equals("new-hash")
                && saved.updatedAt().equals(clock.instant())));
        verify(resetTokens).deleteById(stored.id());
    }

    @Test
    void confirmResetRejectsUnknownToken() {
        when(resetTokens.findByTokenHash(anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidResetTokenException.class,
                () -> service.confirmReset("unknown-token-value-that-is-long-enough", "new-password"));
    }

    @Test
    void changePasswordRequiresCurrentPassword() {
        User user = user();
        when(users.findByEmail(user.email())).thenReturn(Optional.of(user));
        when(encoder.matches("wrong-password", user.passwordHash())).thenReturn(false);

        assertThrows(InvalidCurrentPasswordException.class,
                () -> service.changePassword(user.email(), "wrong-password", "new-password"));
        verify(users, never()).save(any());
    }

    @Test
    void changePasswordStoresNewHash() {
        User user = user();
        when(users.findByEmail(user.email())).thenReturn(Optional.of(user));
        when(encoder.matches("old-password", user.passwordHash())).thenReturn(true);
        when(encoder.encode("new-password")).thenReturn("new-hash");
        when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.changePassword(user.email(), "old-password", "new-password");

        verify(users).save(argThat(saved -> saved.passwordHash().equals("new-hash")));
        verify(resetTokens).deleteByUserId(user.id());
    }

    private PasswordService.PasswordResetResult issueFor(User user) {
        when(users.findByEmail(user.email())).thenReturn(Optional.of(user));
        when(resetTokens.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        return service.requestReset(user.email());
    }

    private PasswordResetToken captureSavedToken() {
        var captor = org.mockito.ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(resetTokens).save(captor.capture());
        return captor.getValue();
    }

    private static User user() {
        Instant instant = Instant.parse("2026-09-30T10:00:00Z");
        return new User(UUID.fromString("db490275-6bd7-433b-8564-7291a92d0af3"),
                "Ana Pérez", "ana@example.com", "old-hash", instant, instant);
    }
}
