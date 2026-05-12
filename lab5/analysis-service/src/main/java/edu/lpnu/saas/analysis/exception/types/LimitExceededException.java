package edu.lpnu.saas.analysis.exception.types;

import edu.lpnu.saas.common.exception.types.GeneralWebException;
import org.springframework.http.HttpStatus;

public class LimitExceededException extends GeneralWebException {
    public LimitExceededException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED);
    }
}
