package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends GeneralWebException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
