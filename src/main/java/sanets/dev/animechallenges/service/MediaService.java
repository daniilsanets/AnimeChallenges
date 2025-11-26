package sanets.dev.animechallenges.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.exception.MediaNotDeletedException;
import sanets.dev.animechallenges.exception.MediaNotFoundException;
import sanets.dev.animechallenges.exception.MediaNotUploadedException;
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

    public MediaService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.base-url}") String baseUrl,
            MediaRepository mediaRepository) {
        this.uploadDir = uploadDir;
        this.baseUrl = baseUrl;
        this.mediaRepository = mediaRepository;
    }

    @PostConstruct
    public void initMedia() {
        if (!Files.exists(Path.of(uploadDir))) {
            try {
                Files.createDirectories(Path.of(uploadDir));
            } catch (IOException e) {
                String msg = DIRECTORY_NOT_CREATED_MSG + " " + e.getMessage();
                log.error(msg);
                throw new RuntimeException(msg);
            }
        }
    }

    @Transactional
    public void delete(UUID mediaId) throws MediaNotFoundException{
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(MEDIA_NOT_FOUND_MSG));

        String storageKey = media.getStorageKey();

        Path uploadPath = Paths.get(uploadDir).resolve(storageKey);
        mediaRepository.deleteById(mediaId);
        try {
            Files.deleteIfExists(uploadPath);
        } catch (IOException e){
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

    private String getStorageKey(MultipartFile file){
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".")
                ? originalFileName.substring(originalFileName.lastIndexOf('.'))
                : "";
        return UUID.randomUUID().toString() + extension;
    }

    public Media upload(MultipartFile file) throws MediaNotUploadedException{

        String storageKey = getStorageKey(file);

        Path filePath;

        try {
            Path uploadDirPath = Paths.get(uploadDir);

            filePath = uploadDirPath.resolve(storageKey);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new MediaNotUploadedException(MEDIA_NOT_UPLOADED_TO_SERVER_MSG);
        }

        String webUrl = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + storageKey;

        Media media = Media.builder()
                .storageKey(storageKey)
                .url(webUrl)
                .mimeType(file.getContentType())
                .size(file.getSize())
                .build();

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

}
