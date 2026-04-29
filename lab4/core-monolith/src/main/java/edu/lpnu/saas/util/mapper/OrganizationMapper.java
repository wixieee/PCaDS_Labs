package edu.lpnu.saas.util.mapper;

import edu.lpnu.saas.dto.request.OrganizationRequest;
import edu.lpnu.saas.dto.response.OrganizationResponse;
import edu.lpnu.saas.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OrganizationMapper {
    Organization toOrganization(OrganizationRequest request);
    OrganizationResponse toOrganizationResponse(Organization organization);
    void updateEntityFromDto(OrganizationRequest request, @MappingTarget Organization organization);
}
