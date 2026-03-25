package edu.lpnu.saas.service;

import edu.lpnu.saas.dto.request.OrganizationRequest;
import edu.lpnu.saas.dto.response.OrganizationResponse;
import edu.lpnu.saas.exception.types.NotFoundException;
import edu.lpnu.saas.model.Membership;
import edu.lpnu.saas.model.Organization;
import edu.lpnu.saas.model.ResourceLimit;
import edu.lpnu.saas.model.Subscription;
import edu.lpnu.saas.model.enums.Role;
import edu.lpnu.saas.model.enums.SubscriptionPlan;
import edu.lpnu.saas.model.enums.SubscriptionStatus;
import edu.lpnu.saas.repository.MembershipRepository;
import edu.lpnu.saas.repository.OrganizationRepository;
import edu.lpnu.saas.repository.ResourceLimitRepository;
import edu.lpnu.saas.repository.SubscriptionRepository;
import edu.lpnu.saas.util.mapper.OrganizationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ResourceLimitRepository resourceLimitRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationResponse createOrganization(OrganizationRequest request, Long currentUserId) {
        Organization organization = organizationMapper.toOrganization(request);
        organization = organizationRepository.save(organization);

        Membership membership = Membership.builder()
                .userId(currentUserId)
                .organizationId(organization.getId())
                .role(Role.OWNER)
                .build();
        membershipRepository.save(membership);

        Subscription subscription = Subscription.builder()
                .organizationId(organization.getId())
                .plan(SubscriptionPlan.FREE)
                .status(SubscriptionStatus.ACTIVE)
                .startTime(Instant.now())
                .endTime(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
        subscriptionRepository.save(subscription);

        ResourceLimit limit = ResourceLimit.builder()
                .organizationId(organization.getId())
                .maxCampaigns(SubscriptionPlan.FREE.getMaxCampaigns())
                .maxComments(SubscriptionPlan.FREE.getMaxCommentsPerMonth())
                .usedCampaignsCount(0)
                .usedCommentsCount(0)
                .build();
        resourceLimitRepository.save(limit);

        return organizationMapper.toOrganizationResponse(organization);
    }

    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = findOrganizationById(id);
        return organizationMapper.toOrganizationResponse(organization);
    }

    public List<OrganizationResponse> getUserOrganizations(Long currentUserId) {
        List<Membership> memberships = membershipRepository.findByUserId(currentUserId);

        return memberships.stream()
                .map(Membership::getOrganizationId)
                .map(organizationRepository::findById)
                .flatMap(Optional::stream)
                .map(organizationMapper::toOrganizationResponse)
                .collect(Collectors.toList());
    }

    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        Organization organization = findOrganizationById(id);

        organizationMapper.updateEntityFromDto(request, organization);

        organization = organizationRepository.save(organization);
        return organizationMapper.toOrganizationResponse(organization);
    }

    public void deleteOrganization(Long id) {
        Organization organization = findOrganizationById(id);
        organizationRepository.deleteById(organization.getId());
    }

    private Organization findOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Організацію не знайдено"));
    }
}