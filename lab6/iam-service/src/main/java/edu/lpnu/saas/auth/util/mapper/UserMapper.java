package edu.lpnu.saas.auth.util.mapper;

import edu.lpnu.saas.auth.dto.RegistrationRequest;
import edu.lpnu.saas.auth.dto.UserResponse;
import edu.lpnu.saas.auth.model.User;
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
    UserResponse toResponse(User user);
}
