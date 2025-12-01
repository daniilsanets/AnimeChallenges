package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.model.Media;

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
}
