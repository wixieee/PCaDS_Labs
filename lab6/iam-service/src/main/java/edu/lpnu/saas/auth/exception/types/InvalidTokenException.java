package edu.lpnu.saas.auth.exception.types;

import edu.lpnu.saas.common.exception.types.GeneralWebException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends GeneralWebException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
