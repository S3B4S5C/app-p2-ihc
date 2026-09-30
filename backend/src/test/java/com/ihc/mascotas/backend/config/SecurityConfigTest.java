package com.ihc.mascotas.backend.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityConfigTest {

    @Test
    void jwtSecretMustHaveAtLeast32Bytes() {
        SecurityConfig config = new SecurityConfig();
        assertThrows(IllegalArgumentException.class, () -> config.jwtSecretKey("too-short"));
    }
}
