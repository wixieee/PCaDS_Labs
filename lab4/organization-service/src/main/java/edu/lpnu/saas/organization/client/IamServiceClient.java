package edu.lpnu.saas.organization.client;

import edu.lpnu.saas.organization.dto.UserOrganizationsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import edu.lpnu.saas.organization.dto.InternalMembershipRequest;

@FeignClient(name = "iam-service", path = "/internal", fallback = IamServiceFallback.class)
public interface IamServiceClient {

    @PostMapping("/memberships")
    void createInternalMembership(
            @RequestBody InternalMembershipRequest request,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/users/{userId}/organizations")
    UserOrganizationsResponse getUserOrganizationIds(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token
    );
}