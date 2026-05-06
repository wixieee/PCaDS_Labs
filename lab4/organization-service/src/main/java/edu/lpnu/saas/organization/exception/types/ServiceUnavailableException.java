package edu.lpnu.saas.organization.exception.types;

import edu.lpnu.saas.common.exception.types.GeneralWebException;
import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends GeneralWebException {
    public ServiceUnavailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
