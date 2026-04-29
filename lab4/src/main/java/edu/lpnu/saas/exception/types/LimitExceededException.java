package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class LimitExceededException extends GeneralWebException {
    public LimitExceededException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED);
    }
}
