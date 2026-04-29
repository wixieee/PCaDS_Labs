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
public class AnalyzeCommentRequest {
    @NotBlank(message = "Текст коментаря не може бути порожнім")
    @Size(max = 2000, message = "Текст занадто довгий (максимум 2000 символів)")
    private String text;
}