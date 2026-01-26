package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.media.MediaType;

import static sanets.dev.animechallenges.exception.ErrorMessages.INVALID_MEDIA_CASTING_MSG;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface MediaMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "name", "originalFilename", "inputStream", "resource"
    })
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "file.size", target = "size")
    @Mapping(source = "file.contentType", target = "mimeType")
    Media toMedia(MultipartFile file, String storageKey, String url);

    static MediaType mapMimeTypeToMediaType(String mimeType) {
        if (mimeType == null) {
            return MediaType.LINK;
        }
        mimeType = mimeType.toLowerCase();

        if (mimeType.startsWith("image/")) {
            return MediaType.PHOTO;
        } else if (mimeType.startsWith("video/")) {
            return MediaType.VIDEO;
        } else if (mimeType.startsWith("http")) {
            return MediaType.LINK;
        } else {
            throw new IllegalArgumentException(INVALID_MEDIA_CASTING_MSG);
        }
    }
}
