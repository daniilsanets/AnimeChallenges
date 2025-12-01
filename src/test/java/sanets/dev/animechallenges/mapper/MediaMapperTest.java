package sanets.dev.animechallenges.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.mock.web.MockMultipartFile;
import sanets.dev.animechallenges.model.Media;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaMapperTest {

    private MediaMapper mediaMapper = Mappers.getMapper(MediaMapper.class);

    private MockMultipartFile file;

    @BeforeEach
    void setup() {
        file = new MockMultipartFile(
                "file", "test.png", "image/png", "test-content".getBytes()
        );
    }


    @Test
    void mediaMapperTest() {
        String storageKey = "some/storage/key";
        String url = "some/url";

        Media expected = Media.builder()
                .mimeType("image/png")
                .size(file.getSize())
                .storageKey(storageKey)
                .url(url)
                .build();

        Media media = mediaMapper.toMedia(file, storageKey, url);

        assertEquals(expected.getSize(), media.getSize());
        assertEquals(expected.getStorageKey(), media.getStorageKey());
    }
}
