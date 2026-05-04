package edu.lpnu.saas.auth.service;

import edu.lpnu.saas.auth.dto.AuthResponse;
import edu.lpnu.saas.auth.dto.ForgotPasswordRequest;
import edu.lpnu.saas.auth.dto.LoginRequest;
import edu.lpnu.saas.auth.dto.RefreshTokenRequest;
import edu.lpnu.saas.auth.dto.RegistrationRequest;
import edu.lpnu.saas.auth.dto.ResetPasswordRequest;
import edu.lpnu.saas.auth.exception.types.InvalidTokenException;
import edu.lpnu.saas.auth.model.Membership;
import edu.lpnu.saas.auth.model.RefreshToken;
import edu.lpnu.saas.auth.model.User;
import edu.lpnu.saas.auth.model.VerificationToken;
import edu.lpnu.saas.auth.repository.MembershipRepository;
import edu.lpnu.saas.auth.repository.UserRepository;
import edu.lpnu.saas.auth.repository.VerificationTokenRepository;
import edu.lpnu.saas.auth.util.mapper.UserMapper;
import edu.lpnu.saas.common.exception.types.AlreadyExistsException;
import edu.lpnu.saas.common.exception.types.NotFoundException;
import edu.lpnu.saas.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final VerificationTokenRepository tokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        User user = findUserByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Невірний email або пароль");
        }
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Користувач уже існує");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByTokenOrThrow(request.getToken());
        refreshTokenService.verifyExpiration(refreshToken);

        User user = findUserById(refreshToken.getUserId());
        refreshTokenService.deleteByToken(refreshToken.getToken());

        return generateAuthResponse(user);
    }

    public void processForgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String tokenString = UUID.randomUUID().toString();

            VerificationToken token = VerificationToken.builder()
                    .token(tokenString)
                    .userId(user.getId())
                    .expiryDate(Instant.now().plus(15, ChronoUnit.MINUTES))
                    .build();

            tokenRepository.save(token);
            // TODO: Замінити на відправку події в RabbitMQ
            log.info("Створено токен скидання пароля для {}: {}", user.getEmail(), tokenString);
        });
    }

    @Transactional
    public void processResetPassword(ResetPasswordRequest request) {
        VerificationToken token = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Недійсний токен"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            tokenRepository.deleteByToken(token.getToken());
            throw new InvalidTokenException("Час дії токена минув");
        }

        User user = findUserById(token.getUserId());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.deleteByToken(token.getToken());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Користувача не знайдено"));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Користувача не знайдено"));
    }

    private AuthResponse generateAuthResponse(User user) {
        List<Membership> memberships = membershipRepository.findByUserId(user.getId());

        Map<String, String> rolesMap = new HashMap<>();
        for (Membership m : memberships) {
            rolesMap.put(m.getOrganizationId().toString(), m.getRole().name());
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("memberships", rolesMap);
        extraClaims.put("userId", user.getId());

        String accessToken = jwtService.generateToken(extraClaims, user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        return new AuthResponse()
                .accessToken(accessToken)
                .refreshToken(refreshToken);
    }
}
