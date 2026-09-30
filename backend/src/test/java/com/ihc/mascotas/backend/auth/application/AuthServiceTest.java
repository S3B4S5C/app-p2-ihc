package com.ihc.mascotas.backend.auth.application;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ihc.mascotas.backend.auth.domain.User;
import com.ihc.mascotas.backend.auth.domain.UserRepository;
import com.ihc.mascotas.backend.shared.exception.ConflictException;
import com.ihc.mascotas.backend.shared.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final TokenService tokenService = mock(TokenService.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-30T12:00:00Z"), ZoneOffset.UTC);
    private AuthService service;

    @BeforeEach
    void setUp() {
        reset(users, passwordEncoder, tokenService);
        service = new AuthService(users, passwordEncoder, tokenService, clock);
    }

    @Test
    void registerNormalizesEmailHashesPasswordAndReturnsToken() {
        when(users.existsByEmail("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.issue(any(User.class))).thenReturn(
                new TokenService.IssuedToken("jwt-token", Instant.parse("2026-09-30T20:00:00Z")));

        AuthService.AuthResult result = service.register(" Ana Pérez ", " ANA@Example.COM ", "password123");

        assertEquals("jwt-token", result.accessToken());
        assertEquals("Ana Pérez", result.user().fullName());
        assertEquals("ana@example.com", result.user().email());
        verify(passwordEncoder).encode("password123");
        verify(users).save(argThat(user -> user.passwordHash().equals("hashed")
                && user.createdAt().equals(Instant.parse("2026-09-30T12:00:00Z"))));
    }

    @Test
    void registerRejectsDuplicatedEmail() {
        when(users.existsByEmail("ana@example.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> service.register("Ana", "ana@example.com", "password123"));
        verify(users, never()).save(any());
    }

    @Test
    void loginReturnsTokenWhenCredentialsMatch() {
        User user = user();
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(tokenService.issue(user)).thenReturn(
                new TokenService.IssuedToken("jwt-token", Instant.parse("2026-09-30T20:00:00Z")));

        AuthService.AuthResult result = service.login("ANA@example.com", "password123");

        assertEquals("jwt-token", result.accessToken());
        assertEquals(user.id(), result.user().id());
    }

    @Test
    void loginDoesNotRevealWhetherEmailExists() {
        when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        BadCredentialsException error = assertThrows(BadCredentialsException.class,
                () -> service.login("missing@example.com", "password123"));
        assertEquals("Credenciales inválidas", error.getMessage());
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = user();
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-pass", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> service.login("ana@example.com", "wrong-pass"));
    }

    @Test
    void currentUserReturnsSafeViewWithoutPassword() {
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(user()));

        AuthService.UserView view = service.currentUser("ana@example.com");

        assertEquals("Ana Pérez", view.fullName());
        assertEquals("ana@example.com", view.email());
    }

    @Test
    void currentUserFailsIfJwtSubjectNoLongerExists() {
        when(users.findByEmail("deleted@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.currentUser("deleted@example.com"));
    }

    private static User user() {
        Instant instant = Instant.parse("2026-09-30T12:00:00Z");
        return new User(UUID.fromString("db490275-6bd7-433b-8564-7291a92d0af3"),
                "Ana Pérez", "ana@example.com", "hashed", instant, instant);
    }
}
