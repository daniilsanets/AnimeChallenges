package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.badge.BadgeRequestDto;
import sanets.dev.animechallenges.dto.badge.BadgeResponseDto;
import sanets.dev.animechallenges.model.badge.Badge;
import sanets.dev.animechallenges.model.media.Media;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface BadgeMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "uid","code", "rule", "active", "createdAt", "updatedAt"
    })
    @Mapping(source = "image", target = "imageUrl")
    BadgeResponseDto toBadgeResponseDto(Badge badge);

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "uid", "storageKey", "url", "mimeType", "size", "createdAt", "updatedAt",
            "empty", "bytes", "blank"
    })
    @Mapping(source = "media", target = "image")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Badge toBadge(BadgeRequestDto badgeRequestDto, Media media, String code);

    default String mapMediaToUrl(Media media) {
        return media == null ? null : media.getUrl();
    }
}
