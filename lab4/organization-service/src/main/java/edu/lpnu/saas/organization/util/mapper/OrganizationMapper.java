package edu.lpnu.saas.organization.util.mapper;

import edu.lpnu.saas.organization.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import edu.lpnu.saas.organization.dto.OrganizationRequest;
import edu.lpnu.saas.organization.dto.OrganizationResponse;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OrganizationMapper {
    Organization toOrganization(OrganizationRequest request);
    OrganizationResponse toOrganizationResponse(Organization organization);
    void updateEntityFromDto(OrganizationRequest request, @MappingTarget Organization organization);
}
