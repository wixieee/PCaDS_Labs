package edu.lpnu.saas.controller;

import edu.lpnu.saas.dto.request.ChangePasswordRequest;
import edu.lpnu.saas.dto.request.UpdateProfileRequest;
import edu.lpnu.saas.dto.response.UserResponse;
import edu.lpnu.saas.security.JwtPrincipal;
import edu.lpnu.saas.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
@Tag(name = "User Profile")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Отримати дані свого профілю")
    public ResponseEntity<@NonNull UserResponse> getMyProfile(Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getProfile(principal.getId()));
    }

    @PutMapping
    @Operation(summary = "Оновити дані свого профілю")
    public ResponseEntity<@NonNull UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        UserResponse response = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    @Operation(summary = "Змінити свій пароль")
    public ResponseEntity<@NonNull Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        userService.changePassword(principal.getId(), request);
        return ResponseEntity.ok().build();
    }
}