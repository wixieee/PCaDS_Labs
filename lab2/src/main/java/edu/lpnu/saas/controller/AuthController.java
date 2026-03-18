package edu.lpnu.saas.controller;

import edu.lpnu.saas.dto.request.ForgotPasswordRequest;
import edu.lpnu.saas.dto.request.LoginRequest;
import edu.lpnu.saas.dto.request.RefreshTokenRequest;
import edu.lpnu.saas.dto.request.RegistrationRequest;
import edu.lpnu.saas.dto.request.ResetPasswordRequest;
import edu.lpnu.saas.dto.response.AuthResponse;
import edu.lpnu.saas.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<@NonNull AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<@NonNull AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<@NonNull AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<@NonNull Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.processForgotPassword(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<@NonNull Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.processResetPassword(request);
        return ResponseEntity.ok().build();
    }
}