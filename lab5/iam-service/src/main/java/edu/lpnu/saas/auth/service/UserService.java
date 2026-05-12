package edu.lpnu.saas.auth.service;

import edu.lpnu.saas.auth.dto.ChangePasswordRequest;
import edu.lpnu.saas.auth.dto.UpdateProfileRequest;
import edu.lpnu.saas.auth.dto.UserResponse;
import edu.lpnu.saas.auth.model.User;
import edu.lpnu.saas.auth.repository.UserRepository;
import edu.lpnu.saas.auth.util.mapper.UserMapper;
import edu.lpnu.saas.common.exception.types.AlreadyExistsException;
import edu.lpnu.saas.common.exception.types.BadRequestException;
import edu.lpnu.saas.common.exception.types.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(Long userId) {
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findUserById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Старий пароль вказано невірно");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserById(userId);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new AlreadyExistsException("Користувач з таким email вже існує");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Користувача не знайдено"));
    }
}