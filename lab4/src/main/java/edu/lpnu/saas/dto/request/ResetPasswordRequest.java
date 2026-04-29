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
public class ResetPasswordRequest {
    @NotBlank(message = "Токен обов'язковий")
    private String token;

    @NotBlank(message = "Новий пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль має містити мінімум 6 символів")
    private String newPassword;
}