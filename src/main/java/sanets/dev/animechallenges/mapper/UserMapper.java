package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.user.AdminUserProfileResponseDto;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;
import sanets.dev.animechallenges.model.user.User;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
           "uid", "passwordHash", "createdAt", "updatedAt"
    })
    @Mapping(source = "user.avatar.uid", target = "avatarUid")
    AdminUserProfileResponseDto toAdminUserProfileResponseDto(User user);

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "uid", "passwordHash", "createdAt", "updatedAt","email", "role", "isActive"
    })
    @Mapping(source = "user.avatar.uid", target = "avatarUid")
    UserProfileResponseDto toUserProfileResponseDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                 nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
                ignoreUnmappedSourceProperties = {
                        "avatarUid"
                })
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt" , ignore = true)
    @Mapping(target = "avatar", ignore = true)
    void updateUserProfileFromDto(UpdateUserProfileRequestDto userProfileRequestDto, @MappingTarget User user);
}
