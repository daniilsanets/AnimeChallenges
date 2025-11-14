package sanets.dev.animechallenges.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sanets.dev.animechallenges.dto.SignUpRequestDto;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "createdAt", source = "now")
    @Mapping(target = "updatedAt", source = "now")

    @Mapping(target = "uid", ignore = true)
    User signupDtoToUser(SignUpRequestDto dto, String passwordHash, UserRole role, OffsetDateTime now);

}
