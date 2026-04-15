package edu.lpnu.saas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    @Size(max = 50, message = "Ім'я занадто довге")
    private String firstName;

    @Size(max = 50, message = "Прізвище занадто довге")
    private String lastName;

    @Email(message = "Некоректний формат email")
    private String email;
}
