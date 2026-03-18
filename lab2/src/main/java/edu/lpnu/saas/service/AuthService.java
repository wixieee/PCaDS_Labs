package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.request.ForgotPasswordRequest;
import edu.lpnu.saas.dto.request.LoginRequest;
import edu.lpnu.saas.dto.request.RefreshTokenRequest;
import edu.lpnu.saas.dto.request.RegistrationRequest;
import edu.lpnu.saas.dto.request.ResetPasswordRequest;
import edu.lpnu.saas.dto.response.AuthResponse;
import edu.lpnu.saas.exception.types.InvalidTokenException;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.Membership;
import edu.lpnu.saas.model.RefreshToken;
import edu.lpnu.saas.model.User;
import edu.lpnu.saas.model.VerificationToken;
import edu.lpnu.saas.repository.MembershipRepository;
import edu.lpnu.saas.repository.UserRepository;
import edu.lpnu.saas.repository.VerificationTokenRepository;
import edu.lpnu.saas.security.JWTService;
import edu.lpnu.saas.security.RefreshTokenService;
import edu.lpnu.saas.security.UserDetailsImpl;
import edu.lpnu.saas.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final VerificationTokenRepository tokenRepository;
    private final SmtpEmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final JWTService jwtService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = findUserByEmail(request.getEmail());

        return generateAuthResponse(user);
    }

    public AuthResponse register(RegistrationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return generateAuthResponse(user);
    }

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
            emailService.sendPasswordResetEmail(user.getEmail(), tokenString);
        });
    }

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

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String accessToken = jwtService.generateToken(extraClaims, userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
