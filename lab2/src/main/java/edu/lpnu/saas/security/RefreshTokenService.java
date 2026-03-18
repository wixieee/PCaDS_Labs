package edu.lpnu.saas.security;

import edu.lpnu.saas.exception.types.InvalidTokenException;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.RefreshToken;
import edu.lpnu.saas.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.refresh.expiry}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByTokenOrThrow(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Refresh token не знайдено"));
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.deleteByUserId(token.getUserId());
            throw new InvalidTokenException("Refresh token протерміновано. Будь ласка, залогіньтесь знову.");
        }
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}