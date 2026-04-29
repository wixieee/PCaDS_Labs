package edu.lpnu.saas.controller;

import edu.lpnu.saas.aop.AuditAction;
import edu.lpnu.saas.dto.request.AnalyzeCommentRequest;
import edu.lpnu.saas.dto.response.AnalyzeCommentResponse;
import edu.lpnu.saas.service.SemanticAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/organizations/{organizationId}/analysis")
@RequiredArgsConstructor
@Tag(name = "Semantic Analysis")
public class SemanticAnalysisController {

    private final SemanticAnalysisService analysisService;

    @PostMapping("/comment")
    @PreAuthorize("@orgSecurity.hasMinRole(#organizationId, 'ANALYST')")
    @AuditAction(action = "COMMENT_ANALYZE", orgId = "#organizationId")
    @Operation(summary = "Проаналізувати семантику тексту коментаря")
    public ResponseEntity<@NonNull AnalyzeCommentResponse> analyze(
            @PathVariable Long organizationId,
            @Valid @RequestBody AnalyzeCommentRequest request
    ) {
        AnalyzeCommentResponse response = analysisService.analyzeComment(organizationId, request);
        return ResponseEntity.ok(response);
    }
}