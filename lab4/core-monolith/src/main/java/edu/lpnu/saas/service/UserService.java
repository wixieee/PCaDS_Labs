package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.request.ChangePasswordRequest;
import edu.lpnu.saas.dto.request.UpdateProfileRequest;
import edu.lpnu.saas.dto.response.UserResponse;
import edu.lpnu.saas.exception.types.AlreadyExistsException;
import edu.lpnu.saas.exception.types.BadRequestException;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.User;
import edu.lpnu.saas.repository.UserRepository;
import edu.lpnu.saas.util.mapper.UserMapper;
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