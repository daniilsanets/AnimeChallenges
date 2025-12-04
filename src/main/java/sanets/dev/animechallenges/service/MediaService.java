package sanets.dev.animechallenges.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.exception.media.MediaNotDeletedException;
import sanets.dev.animechallenges.exception.media.MediaNotFoundException;
import sanets.dev.animechallenges.exception.media.MediaNotUploadedException;
import sanets.dev.animechallenges.mapper.MediaMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.repository.MediaRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class MediaService {

    private static final String MEDIA_NOT_FOUND_MSG = "Media not found";
    private static final String MEDIA_NOT_UPLOADED_TO_SERVER_MSG = "Media not uploaded to server storage";
    private static final String MEDIA_NOT_DELETED_FROM_SERVER_MSG = "Media not deleted from server storage";
    private static final String DIRECTORY_NOT_CREATED_MSG = "Could not initialize storage location";

    private final String uploadDir;
    private final String baseUrl;
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;

    public MediaService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.base-url}") String baseUrl,
            MediaRepository mediaRepository,
            MediaMapper mediaMapper) {
        this.uploadDir = uploadDir;
        this.baseUrl = baseUrl;
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
    }

    @PostConstruct
    public void initMedia() {
        if (!Files.exists(Path.of(uploadDir))) {
            try {
                Files.createDirectories(Path.of(uploadDir));
            } catch (IOException e) {
                String msg = DIRECTORY_NOT_CREATED_MSG + " " + e.getMessage();
                log.error("Error to create dir: {}",msg);
                throw new RuntimeException(msg);
            }
        }
    }

    public void delete(UUID mediaId) throws MediaNotFoundException{
        log.debug("Invoke delete media {}", mediaId);
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(MEDIA_NOT_FOUND_MSG));
        log.debug("The media was found");
        String storageKey = media.getStorageKey();

        Path uploadPath = Paths.get(uploadDir).resolve(storageKey);
        mediaRepository.deleteById(mediaId);
        try {
            Files.deleteIfExists(uploadPath);
        } catch (IOException e){
            log.error("Error to delete media: {}",e.getMessage());
            throw new MediaNotDeletedException(MEDIA_NOT_DELETED_FROM_SERVER_MSG);
        }

    }

    public void deleteFileOnly(String storageKey) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(storageKey);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error(MEDIA_NOT_DELETED_FROM_SERVER_MSG, e);
        }
    }

    public Media upload(MultipartFile file) throws MediaNotUploadedException{
        String storageKey = createStorageKey(file);
        Path filePath = saveFile(file, storageKey);

        String webUrl = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + storageKey;

        //Would you make here mapper for media or just leave it like it was?
        Media media = mediaMapper.toMedia(file, storageKey, webUrl);

        try {
            mediaRepository.save(media);
        } catch (DataAccessException ex) {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("CRITICAL: Failed to clean up file {} after DB error", filePath, e);
            }
            String msg = MEDIA_NOT_UPLOADED_TO_SERVER_MSG + " " + ex.getMessage();
            throw new MediaNotUploadedException(msg);
        }

        return media;
    }

    private String createStorageKey(MultipartFile file){
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf('.'))
                : "";
        log.debug("Create storage key");
        return UUID.randomUUID().toString() + extension;
    }

    private Path saveFile(MultipartFile file, String storageKey){
        Path filePath;

        try {
            Path uploadDirPath = Paths.get(uploadDir);

            filePath = uploadDirPath.resolve(storageKey);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath;
        } catch (IOException ex) {
            log.error("Error to upload file: ", ex);
            throw new MediaNotUploadedException(MEDIA_NOT_UPLOADED_TO_SERVER_MSG);
        }
    }

}
