package edu.lpnu.saas.exception.types;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralWebException extends RuntimeException {
    private final HttpStatus status;

    public GeneralWebException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
