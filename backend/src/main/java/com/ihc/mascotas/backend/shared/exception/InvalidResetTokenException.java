package com.ihc.mascotas.backend.shared.exception;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("El token de recuperación no es válido o expiró");
    }
}
