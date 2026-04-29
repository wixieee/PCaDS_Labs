package edu.lpnu.saas.util.scheduler;

import edu.lpnu.saas.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.debug("Початок очищення протермінованих refresh токенів...");
        long deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        log.debug("Успішно видалено {} старих токенів.", deletedCount);
    }
}