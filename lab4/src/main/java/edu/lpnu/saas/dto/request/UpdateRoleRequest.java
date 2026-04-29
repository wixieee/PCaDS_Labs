package edu.lpnu.saas.dto.request;

import edu.lpnu.saas.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {
    @NotNull(message = "Роль не може бути порожньою")
    private Role role;
}