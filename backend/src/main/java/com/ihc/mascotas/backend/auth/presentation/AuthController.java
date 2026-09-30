package com.ihc.mascotas.backend.auth.presentation;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ihc.mascotas.backend.auth.application.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return toResponse(authService.register(request.fullName(), request.email(), request.password()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return toResponse(authService.login(request.email(), request.password()));
    }

    @GetMapping("/me")
    public UserResponse me(Principal principal) {
        return toResponse(authService.currentUser(principal.getName()));
    }

    private static AuthResponse toResponse(AuthService.AuthResult result) {
        long expiresInSeconds = Math.max(0, Duration.between(Instant.now(), result.expiresAt()).toSeconds());
        return new AuthResponse(result.accessToken(), "Bearer", expiresInSeconds, toResponse(result.user()));
    }

    private static UserResponse toResponse(AuthService.UserView user) {
        return new UserResponse(user.id(), user.fullName(), user.email());
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 2, max = 120) String fullName,
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(min = 8, max = 72) String password) { }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(min = 8, max = 72) String password) { }

    public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds, UserResponse user) { }
    public record UserResponse(UUID id, String fullName, String email) { }
}
