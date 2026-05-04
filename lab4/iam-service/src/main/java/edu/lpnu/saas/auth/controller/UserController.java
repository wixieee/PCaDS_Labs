package edu.lpnu.saas.auth.controller;

import edu.lpnu.saas.auth.api.UsersApi;
import edu.lpnu.saas.auth.dto.ChangePasswordRequest;
import edu.lpnu.saas.auth.dto.UpdateProfileRequest;
import edu.lpnu.saas.auth.dto.UserResponse;
import edu.lpnu.saas.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static edu.lpnu.saas.common.security.JwtPrincipal.getCurrentUserId;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getProfile(getCurrentUserId()));
    }

    @Override
    public ResponseEntity<UserResponse> updateProfile(UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(getCurrentUserId(), request));
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
        userService.changePassword(getCurrentUserId(), request);
        return ResponseEntity.ok().build();
    }
}