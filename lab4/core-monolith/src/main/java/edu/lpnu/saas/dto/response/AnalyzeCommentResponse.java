package edu.lpnu.saas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyzeCommentResponse {
    private String originalText;
    private String sentiment;
    private double confidenceScore;
    private boolean isSpam;
}