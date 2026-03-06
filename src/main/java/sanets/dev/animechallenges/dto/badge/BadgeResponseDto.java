package sanets.dev.animechallenges.dto.badge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.badge.BadgeType;

import java.util.UUID;

@Getter
@Setter
@Schema(
        name = "BadgeResponseDto",
        description = "Response payload representing a badge returned by the system."
)
public class BadgeResponseDto {

    @Schema(
            description = "Unique identifier of the badge.",
            example = "550e8400-e29b-41d4-a716-446655440000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID uid;

    @Schema(
            description = "Badge title.",
            example = "Java Master"
    )
    private String title;

    @Schema(
            description = "Detailed description of the badge.",
            example = "Awarded for completing all advanced Java challenges."
    )
    private String description;

    @Schema(
            description = "Type of badge defining its rule interpretation.",
            example = "ACHIEVEMENT"
    )
    private BadgeType badgeType;

    @Schema(
            description = "Public URL pointing to the badge image.",
            example = "https://cdn.example.com/badges/ludaman.png"
    )
    private String imageUrl;
}
