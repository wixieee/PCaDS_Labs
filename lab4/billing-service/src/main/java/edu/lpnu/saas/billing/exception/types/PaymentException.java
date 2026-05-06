package edu.lpnu.saas.billing.exception.types;

import edu.lpnu.saas.common.exception.types.GeneralWebException;
import org.springframework.http.HttpStatus;

public class PaymentException extends GeneralWebException {
    public PaymentException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
