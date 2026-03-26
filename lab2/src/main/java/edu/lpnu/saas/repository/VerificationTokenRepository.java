package edu.lpnu.saas.repository;

import edu.lpnu.saas.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository {
    VerificationToken save(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
    void deleteByToken(String token);
}