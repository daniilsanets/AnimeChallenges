package sanets.dev.animechallenges.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.BadgeRequestDto;
import sanets.dev.animechallenges.dto.BadgeResponseDto;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.Media;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BadgeMapper {
    @Mapping(source = "image", target = "imageUrl")
    BadgeResponseDto toBadgeResponseDto(Badge badge);


    @Mapping(source = "media", target = "image")
    @Mapping(source = "code", target = "code")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Badge toBadge(BadgeRequestDto badgeRequestDto, Media media, String code);

    default String mapMediaToUrl(Media media) {
        return media == null ? null : media.getUrl();
    }
}
