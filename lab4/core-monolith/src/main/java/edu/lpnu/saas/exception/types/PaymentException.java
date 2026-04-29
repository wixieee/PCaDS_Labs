package edu.lpnu.saas.exception.types;

import org.springframework.http.HttpStatus;

public class PaymentException extends GeneralWebException {
    public PaymentException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
