package edu.lpnu.saas.auth.repository;

import edu.lpnu.saas.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(Long userId);
    void deleteByToken(String token);
    long deleteByExpiryDateBefore(Instant now);
}