package edu.lpnu.saas.util.mapper;

import edu.lpnu.saas.dto.request.RegistrationRequest;
import edu.lpnu.saas.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toUser(RegistrationRequest request);
}
