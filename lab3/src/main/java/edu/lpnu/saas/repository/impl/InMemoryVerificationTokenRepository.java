package edu.lpnu.saas.repository.impl;

import edu.lpnu.saas.model.VerificationToken;
import edu.lpnu.saas.repository.VerificationTokenRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryVerificationTokenRepository implements VerificationTokenRepository {

    private final Map<Long, VerificationToken> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public VerificationToken save(VerificationToken token) {
        if (token.getId() == null) {
            token.setId(idGenerator.getAndIncrement());
            token.setCreatedAt(Instant.now());
        }
        token.setUpdatedAt(Instant.now());
        store.put(token.getId(), token);
        return token;
    }

    @Override
    public Optional<VerificationToken> findByToken(String tokenString) {
        return store.values().stream()
                .filter(t -> t.getToken().equals(tokenString))
                .findFirst();
    }

    @Override
    public void deleteByToken(String tokenString) {
        findByToken(tokenString).ifPresent(t -> store.remove(t.getId()));
    }
}