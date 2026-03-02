package sanets.dev.animechallenges.dto.media;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MediaResponseDto {
    private UUID uid;
    private String storageKey;
    private String url;
    private String mimeType;
}
