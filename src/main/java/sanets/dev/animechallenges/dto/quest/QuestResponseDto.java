package sanets.dev.animechallenges.dto.quest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.quest.QuestsDifficulty;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Quest response representation")
public class QuestResponseDto {
    @Schema(
            description = "Quest unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            format = "uuid"
    )
    private UUID uid;

    @Schema(
            description = "Quest title",
            example = "Defeat the Shadow Dragon"
    )
    private String title;

    @Schema(
            description = "Quest description"
    )
    private String description;

    @Schema(
            description = "Difficulty level",
            example = "HARD"
    )
    private QuestsDifficulty difficulty;

    @Schema(
            description = "Reward points",
            example = "5"
    )
    private Integer rewardPoints;

    @Schema(
            description = "Associated badge unique identifier",
            format = "uuid"
    )
    private UUID badgeUid;

    @Schema(
            description = "Maximum allowed attempts",
            example = "3"
    )
    private Integer maxAttempts;

    @Schema(
            description = "Quest creator unique identifier",
            format = "uuid"
    )
    private UUID creatorUid;

    @Schema(
            description = "Indicates whether quest is active",
            example = "true"
    )
    private Boolean active;
}
