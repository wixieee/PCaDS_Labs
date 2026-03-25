package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends GeneralWebException {
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
