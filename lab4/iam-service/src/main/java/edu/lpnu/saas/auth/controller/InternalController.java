package edu.lpnu.saas.auth.controller;

import edu.lpnu.saas.auth.api.InternalApi;
import edu.lpnu.saas.auth.dto.InternalMembershipRequest;
import edu.lpnu.saas.auth.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalController implements InternalApi {

    private final MembershipService membershipService;

    @Override
    public ResponseEntity<Void> createInternalMembership(InternalMembershipRequest request) {
        membershipService.createInternalMembership(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}