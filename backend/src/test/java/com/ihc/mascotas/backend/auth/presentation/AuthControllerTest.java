package com.ihc.mascotas.backend.auth.presentation;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ihc.mascotas.backend.auth.application.AuthService;
import com.ihc.mascotas.backend.auth.application.PasswordService;
import com.ihc.mascotas.backend.shared.exception.GlobalExceptionHandler;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);
    private final PasswordService passwordService = mock(PasswordService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        reset(authService, passwordService);
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, passwordService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registerReturns201AndToken() throws Exception {
        var user = new AuthService.UserView(
                UUID.fromString("db490275-6bd7-433b-8564-7291a92d0af3"), "Ana Pérez", "ana@example.com");
        when(authService.register("Ana Pérez", "ana@example.com", "password123"))
                .thenReturn(new AuthService.AuthResult("token", Instant.now().plusSeconds(3600), user));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Ana Pérez","email":"ana@example.com","password":"password123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("ana@example.com"));
    }

    @Test
    void registerValidatesPayloadBeforeCallingService() throws Exception {
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"A","email":"not-an-email","password":"short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fullName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());

        verifyNoInteractions(authService);
    }

    @Test
    void loginMapsBadCredentialsTo401() throws Exception {
        when(authService.login("ana@example.com", "password123"))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com","password":"password123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos"));
    }
    @Test
    void passwordResetRequestReturnsTemporaryToken() throws Exception {
        when(passwordService.requestReset("ana@example.com")).thenReturn(
                new PasswordService.PasswordResetResult("temporary-reset-token", Instant.parse("2026-09-30T12:15:00Z")));

        mvc.perform(post("/api/auth/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetToken").value("temporary-reset-token"));
    }

    @Test
    void passwordResetConfirmValidatesNewPassword() throws Exception {
        mvc.perform(post("/api/auth/password-reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"temporary-reset-token","newPassword":"short"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.newPassword").exists());

        verifyNoInteractions(passwordService);
    }

}
