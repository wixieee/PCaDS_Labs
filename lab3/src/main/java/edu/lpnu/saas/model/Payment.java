package edu.lpnu.saas.model;

import edu.lpnu.saas.model.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payments")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String stripeSessionId;
}