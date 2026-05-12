package edu.lpnu.saas.common.exception.types;

import org.springframework.http.HttpStatus;

public class BadRequestException extends GeneralWebException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
