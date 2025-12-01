package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.exception.MediaNotUploadedException;
import sanets.dev.animechallenges.mapper.MediaMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.repository.MediaRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private MediaMapper mediaMapper;

    @InjectMocks
    private MediaService mediaService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mediaService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(mediaService, "baseUrl", "/api/v1/media/");

        mediaService.initMedia();
    }

    @Test
    void upload_shouldSaveFileAndEntity_whenSuccessful() {

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "test-content".getBytes()
        );
        when(mediaMapper.toMedia(any(MultipartFile.class), any(String.class), any(String.class)))
                .thenAnswer(invocation -> {
                    String passedStorageKey = invocation.getArgument(1);
                    String passedUrl = invocation.getArgument(2);
                    return Media.builder()
                            .storageKey(passedStorageKey)
                            .url(passedUrl)
                            .mimeType("image/png")
                            .size(100L)
                            .build();
                });

        when(mediaRepository.save(any(Media.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Media result = mediaService.upload(file);

        assertNotNull(result);
        assertNotNull(result.getStorageKey());
        assertTrue(result.getStorageKey().endsWith(".png"));

        Path savedFilePath = tempDir.resolve(result.getStorageKey());
        assertTrue(Files.exists(savedFilePath));

        verify(mediaRepository).save(any(Media.class));
    }

    @Test
    void upload_shouldDeleteFile_whenDatabaseSaveFails() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "fail-db.png", "image/png", "content".getBytes()
        );
        
        when(mediaMapper.toMedia(any(), any(), any()))
                .thenReturn(Media.builder().build());
        when(mediaRepository.save(any(Media.class)))
                .thenThrow(new DataIntegrityViolationException("DB Error"));

        assertThrows(MediaNotUploadedException.class, () -> mediaService.upload(file));

        try (var stream = Files.list(tempDir)) {
            assertEquals(0, stream.count(), "File should be deleted after DB failure");
        } catch (IOException e) {
            fail("Failed to list files");
        }
    }

    @Test
    void delete_shouldRemoveEntityAndFile_whenSuccessful() throws IOException {
        UUID mediaId = UUID.randomUUID();
        String filename = "delete-me.jpg";

        Path filePath = tempDir.resolve(filename);
        Files.createFile(filePath);

        Media media = Media.builder()
                .uid(mediaId)
                .storageKey(filename)
                .build();

        when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(media));

        mediaService.delete(mediaId);

        verify(mediaRepository).deleteById(mediaId);
        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFileOnly_shouldDeleteFile_withoutTouchingDB() throws IOException {
        String filename = "orphan.txt";
        Path filePath = tempDir.resolve(filename);
        Files.createFile(filePath);

        mediaService.deleteFileOnly(filename);

        assertFalse(Files.exists(filePath));
        verifyNoInteractions(mediaRepository);
    }
}