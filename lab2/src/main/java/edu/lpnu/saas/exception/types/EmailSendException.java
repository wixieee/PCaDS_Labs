package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class EmailSendException extends GeneralWebException {
    public EmailSendException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
