package sanets.dev.animechallenges.mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.auth.SignUpRequestDto;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface AuthMapper {

    @Mapping(target = "createdAt", source = "now")
    @Mapping(target = "updatedAt", ignore = true)

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "password",
            "bytes", "empty", "blank",
            "declaringClass",
            "year", "month", "monthValue", "dayOfMonth", "dayOfYear", "dayOfWeek",
            "hour", "minute", "second", "nano", "offset"
    })
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "avatar",  ignore = true)
    @Mapping(target = "bio" ,   ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User signupDtoToUser(SignUpRequestDto dto, String passwordHash, UserRole role, OffsetDateTime now);

}
