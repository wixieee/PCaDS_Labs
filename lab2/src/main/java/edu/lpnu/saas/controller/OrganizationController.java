package edu.lpnu.saas.controller;

import edu.lpnu.saas.dto.request.OrganizationRequest;
import edu.lpnu.saas.dto.response.OrganizationResponse;
import edu.lpnu.saas.security.JwtPrincipal;
import edu.lpnu.saas.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @Operation(summary = "Створити нову організацію")
    public ResponseEntity<@NonNull OrganizationResponse> create(
            @Valid @RequestBody OrganizationRequest request,
            Authentication authentication
    ) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        OrganizationResponse response = organizationService.createOrganization(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Отримати всі свої організації")
    public ResponseEntity<@NonNull List<OrganizationResponse>> getAllMyOrganizations(Authentication authentication) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(organizationService.getUserOrganizations(principal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отримати інформацію про конкретну організацію за її ID")
    public ResponseEntity<@NonNull OrganizationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Оновити назву та billing email організації")
    public ResponseEntity<@NonNull OrganizationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити організацію")
    public ResponseEntity<@NonNull Void> delete(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}