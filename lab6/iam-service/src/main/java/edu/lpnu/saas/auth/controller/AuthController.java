package edu.lpnu.saas.auth.controller;

import edu.lpnu.saas.auth.api.AuthenticationApi;
import edu.lpnu.saas.auth.dto.AuthResponse;
import edu.lpnu.saas.auth.dto.ForgotPasswordRequest;
import edu.lpnu.saas.auth.dto.LoginRequest;
import edu.lpnu.saas.auth.dto.RefreshTokenRequest;
import edu.lpnu.saas.auth.dto.RegistrationRequest;
import edu.lpnu.saas.auth.dto.ResetPasswordRequest;
import edu.lpnu.saas.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthenticationApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> register(RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    public ResponseEntity<AuthResponse> refresh(RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Override
    public ResponseEntity<Void> forgotPassword(ForgotPasswordRequest request) {
        authService.processForgotPassword(request);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> resetPassword(ResetPasswordRequest request) {
        authService.processResetPassword(request);
        return ResponseEntity.ok().build();
    }
}