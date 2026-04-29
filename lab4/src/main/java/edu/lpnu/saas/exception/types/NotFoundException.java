package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class NotFoundException extends GeneralWebException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
