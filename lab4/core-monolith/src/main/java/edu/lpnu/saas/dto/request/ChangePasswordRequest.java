package edu.lpnu.saas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotBlank(message = "Старий пароль обов'язковий")
    private String oldPassword;

    @NotBlank(message = "Новий пароль обов'язковий")
    @Size(min = 6, message = "Пароль має бути не менше 6 символів")
    private String newPassword;
}