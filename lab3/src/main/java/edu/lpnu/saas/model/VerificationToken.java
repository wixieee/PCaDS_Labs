package edu.lpnu.saas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken extends BaseEntity {
    private Long userId;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}