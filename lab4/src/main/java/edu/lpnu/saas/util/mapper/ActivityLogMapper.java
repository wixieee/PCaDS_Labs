package edu.lpnu.saas.util.mapper;

import edu.lpnu.saas.dto.response.ActivityLogResponse;
import edu.lpnu.saas.model.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ActivityLogMapper {
    ActivityLogResponse toResponse(ActivityLog request);
}
