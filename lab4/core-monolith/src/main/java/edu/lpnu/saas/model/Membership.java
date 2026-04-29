package edu.lpnu.saas.model;

import edu.lpnu.saas.model.enums.Role;
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
@Table(name = "memberships")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Membership extends BaseEntity {
    private Long userId;
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    private Role role;
}