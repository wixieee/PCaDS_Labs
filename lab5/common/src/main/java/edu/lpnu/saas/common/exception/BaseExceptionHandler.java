package edu.lpnu.saas.common.exception;

import edu.lpnu.saas.common.exception.types.GeneralWebException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class BaseExceptionHandler {

    @ExceptionHandler(GeneralWebException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleWebException(HttpServletRequest request, GeneralWebException e) {
        return buildErrorResponse(e.getStatus(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleValidation(HttpServletRequest request, MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage() != null ? error.getDefaultMessage() : "Некоректне значення");
        });
        return buildValidationErrorResponse(request.getRequestURI(), errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleMethodNotSupported(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Некоректний метод: " + e.getMethod(), request.getRequestURI());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleNoResource(HttpServletRequest request, NoResourceFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Не знайдено ресурсу для запиту '" + request.getRequestURI() + "'.", request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleNotReadable(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Тіло запиту пусте або некоректне", request.getRequestURI());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleExpiredJwt(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Токен протермінований", request.getRequestURI());
    }

    @ExceptionHandler({SignatureException.class, MalformedJwtException.class, UnsupportedJwtException.class})
    public ResponseEntity<@NonNull ErrorResponse> handleInvalidJwt(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Некоректний токен", request.getRequestURI());
    }

    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<@NonNull ErrorResponse> handleAccessDenied(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "У вас недостатньо прав", request.getRequestURI());
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<@NonNull ErrorResponse> handleAuthException(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Невірний email або пароль", request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<@NonNull ErrorResponse> handleIllegalStateException(HttpServletRequest request, IllegalStateException e) {
        return buildErrorResponse(HttpStatus.EXPECTATION_FAILED, e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ErrorResponse> handleException(HttpServletRequest request, Exception e) {
        log.error("Невідома помилка {}: ", request.getRequestURI(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Щось пішло не так", request.getRequestURI());
    }

    protected ResponseEntity<@NonNull ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {
        return buildResponse(status, message, path, null);
    }

    protected ResponseEntity<@NonNull ErrorResponse> buildValidationErrorResponse(String path, Map<String, String> errors) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Помилка валідації", path, errors);
    }

    private ResponseEntity<@NonNull ErrorResponse> buildResponse(HttpStatus status, String message, String path, Map<String, String> errors) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}