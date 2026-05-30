package edu.lpnu.saas.organization.service;

import edu.lpnu.saas.common.dto.OrganizationCreatedEvent;
import edu.lpnu.saas.common.dto.OrganizationDeletedEvent;
import edu.lpnu.saas.common.exception.types.NotFoundException;
import edu.lpnu.saas.organization.client.IamServiceClient;
import edu.lpnu.saas.organization.dto.InternalMembershipRequest;
import edu.lpnu.saas.organization.dto.OrganizationRequest;
import edu.lpnu.saas.organization.dto.OrganizationResponse;
import edu.lpnu.saas.organization.dto.UserOrganizationsResponse;
import edu.lpnu.saas.organization.model.Organization;
import edu.lpnu.saas.organization.repository.OrganizationRepository;
import edu.lpnu.saas.organization.util.mapper.OrganizationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    private final IamServiceClient iamServiceClient;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @CachePut(value = "organizations", key = "#result.id")
    public OrganizationResponse createOrganization(OrganizationRequest request, Long currentUserId) {
        Organization organization = organizationMapper.toOrganization(request);
        organization = organizationRepository.save(organization);

        InternalMembershipRequest membershipRequest = new InternalMembershipRequest()
                .userId(currentUserId)
                .organizationId(organization.getId())
                .role(InternalMembershipRequest.RoleEnum.OWNER);

        iamServiceClient.createInternalMembership(membershipRequest, getCurrentHttpRequestToken());
        log.info("Успішно створено Membership (OWNER) для юзера {} в організації {}", currentUserId, organization.getId());

        OrganizationCreatedEvent event = OrganizationCreatedEvent.builder()
                .organizationId(organization.getId())
                .ownerId(currentUserId)
                .build();

        rabbitTemplate.convertAndSend("organization.exchange", "organization.created", event);
        log.info("Відправлено подію OrganizationCreatedEvent для організації {}", organization.getId());

        return organizationMapper.toOrganizationResponse(organization);
    }

    @Cacheable(value = "organizations", key = "#id")
    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = findOrganizationById(id);
        return organizationMapper.toOrganizationResponse(organization);
    }

    public List<OrganizationResponse> getUserOrganizations(Long currentUserId) {
        UserOrganizationsResponse iamResponse = iamServiceClient.getUserOrganizationIds(
                currentUserId,
                getCurrentHttpRequestToken()
        );

        List<Long> orgIds = iamResponse.getOrganizationIds();

        if (orgIds == null || orgIds.isEmpty()) {
            return List.of();
        }

        List<Organization> organizations = organizationRepository.findAllById(orgIds);

        return organizations.stream()
                .map(organizationMapper::toOrganizationResponse)
                .toList();
    }

    @Transactional
    @CachePut(value = "organizations", key = "#id")
    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        Organization organization = findOrganizationById(id);
        organizationMapper.updateEntityFromDto(request, organization);
        organization = organizationRepository.save(organization);
        return organizationMapper.toOrganizationResponse(organization);
    }

    @Transactional
    @CacheEvict(value = "organizations", key = "#id")
    public void deleteOrganization(Long id) {
        Organization organization = findOrganizationById(id);

        organizationRepository.deleteById(organization.getId());

        OrganizationDeletedEvent event = OrganizationDeletedEvent.builder()
                .organizationId(id)
                .build();
        rabbitTemplate.convertAndSend("organization.exchange", "organization.deleted", event);
        log.info("Організацію {} видалено. Подію розіслано.", id);
    }

    private Organization findOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Організацію не знайдено"));
    }

    private String getCurrentHttpRequestToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader("Authorization");
        }
        return null;
    }
}