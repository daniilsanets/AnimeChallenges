package sanets.dev.animechallenges.dto.media;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(name = "Media Response DTO", description = "Provide media data")
public class MediaResponseDto {

    @Schema(
            description = "Media UUID",
            example = "550e8400-e29b-41d4-a716-446655440077"
    )
    private UUID uid;


    @Schema(description = "Unique storage identifier of the file inside object storage")
    private String storageKey;

    @Schema(
            description = "Publicly accessible URL of the uploaded media file",
            example = "https://cdn.example.com/media/abc123.jpg"
    )
    private String url;

    @Schema(
            description = "MIME type of the uploaded file",
            example = "image/jpeg",
            allowableValues = {
                    "image/jpeg",
                    "image/png",
                    "image/webp",
                    "video/mp4",
                    "application/pdf"
            }
    )
    private String mimeType;
}
