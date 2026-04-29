package edu.lpnu.saas.dto.request;

import jakarta.validation.constraints.Email;
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
public class RegistrationRequest {
    @NotBlank(message = "Ім'я не може бути порожнім")
    @Size(max = 50, message = "Ім'я занадто довге")
    private String firstName;

    @NotBlank(message = "Прізвище не може бути порожнім")
    @Size(max = 50, message = "Прізвище занадто довге")
    private String lastName;

    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Некоректний формат email")
    private String email;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min = 6, message = "Пароль має містити мінімум 6 символів")
    private String password;
}
