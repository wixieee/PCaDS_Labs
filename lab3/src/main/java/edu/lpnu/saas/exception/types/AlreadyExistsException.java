package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends GeneralWebException {
    public AlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
