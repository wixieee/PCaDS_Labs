package edu.lpnu.saas.model;

import edu.lpnu.saas.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Membership extends BaseEntity {
    private Long userId;
    private Long organizationId;
    private Role role;
}
