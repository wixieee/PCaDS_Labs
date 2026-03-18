package edu.lpnu.saas.model;

import edu.lpnu.saas.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {
    private Long organizationId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
}
