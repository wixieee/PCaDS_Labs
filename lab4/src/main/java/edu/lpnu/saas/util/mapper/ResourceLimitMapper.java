package edu.lpnu.saas.util.mapper;

import edu.lpnu.saas.dto.response.ResourceLimitResponse;
import edu.lpnu.saas.model.ResourceLimit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ResourceLimitMapper {
    ResourceLimitResponse toResponse (ResourceLimit request);
}
