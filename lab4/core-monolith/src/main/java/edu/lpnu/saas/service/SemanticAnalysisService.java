package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.request.AnalyzeCommentRequest;
import edu.lpnu.saas.dto.response.AnalyzeCommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticAnalysisService {

    private final ResourceLimitService resourceLimitService;
    private final Random random = new Random();

    public AnalyzeCommentResponse analyzeComment(Long organizationId, AnalyzeCommentRequest request) {
        resourceLimitService.consumeComment(organizationId);

        String[] sentiments = {"POSITIVE", "NEGATIVE", "NEUTRAL"};
        String randomSentiment = sentiments[random.nextInt(sentiments.length)];
        double confidence = 0.60 + (0.39 * random.nextDouble());
        boolean isSpam = random.nextInt(100) < 5;

        return AnalyzeCommentResponse.builder()
                .originalText(request.getText())
                .sentiment(randomSentiment)
                .confidenceScore(Math.round(confidence * 100.0) / 100.0)
                .isSpam(isSpam)
                .build();
    }
}