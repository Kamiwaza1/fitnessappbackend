package com.zeki.demo.users.exception;

public class AuthenticationValidationException extends RuntimeException {
    public AuthenticationValidationException(String message) {
        super(message);
    }
}
