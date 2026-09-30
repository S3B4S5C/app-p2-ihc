package com.ihc.mascotas.backend.shared.exception;

public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() {
        super("La contraseña actual es incorrecta");
    }
}
