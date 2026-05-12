package edu.lpnu.saas.analysis.controller;

import edu.lpnu.saas.analysis.api.SemanticAnalysisApi;
import edu.lpnu.saas.analysis.dto.AnalyzeCommentRequest;
import edu.lpnu.saas.analysis.dto.AnalyzeCommentResponse;
import edu.lpnu.saas.analysis.service.SemanticAnalysisService;
import edu.lpnu.saas.common.aop.AuditAction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SemanticAnalysisController implements SemanticAnalysisApi {

    private final SemanticAnalysisService analysisService;

    @Override
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'ANALYST')")
    @AuditAction(action = "COMMENT_ANALYZE", orgId = "#organizationId")
    public ResponseEntity<AnalyzeCommentResponse> analyze(Long organizationId, AnalyzeCommentRequest request) {
        AnalyzeCommentResponse response = analysisService.analyzeComment(organizationId, request);
        return ResponseEntity.ok(response);
    }
}
