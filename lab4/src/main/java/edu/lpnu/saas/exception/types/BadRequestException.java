package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class BadRequestException extends GeneralWebException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
