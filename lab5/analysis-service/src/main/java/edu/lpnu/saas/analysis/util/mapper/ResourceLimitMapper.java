package edu.lpnu.saas.analysis.util.mapper;

import edu.lpnu.saas.analysis.dto.ResourceLimitResponse;
import edu.lpnu.saas.analysis.model.ResourceLimit;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ResourceLimitMapper {
    ResourceLimitResponse toResponse(ResourceLimit limit);
}