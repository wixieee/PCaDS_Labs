package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.RefreshToken;
import edu.lpnu.saas.repository.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<Long, RefreshToken> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public RefreshToken save(RefreshToken token) {
        if (token.getId() == null) {
            token.setId(idGenerator.getAndIncrement());
            token.setCreatedAt(Instant.now());
        }
        token.setUpdatedAt(Instant.now());
        store.put(token.getId(), token);
        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(String tokenString) {
        return store.values().stream()
                .filter(t -> t.getToken().equals(tokenString))
                .findFirst();
    }

    @Override
    public void deleteByToken(String tokenString) {
        findByToken(tokenString).ifPresent(t -> store.remove(t.getId()));
    }

    @Override
    public void deleteByUserId(Long userId) {
        store.values().removeIf(t -> t.getUserId().equals(userId));
    }
}