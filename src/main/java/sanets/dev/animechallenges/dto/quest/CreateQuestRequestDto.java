package sanets.dev.animechallenges.dto.quest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sanets.dev.animechallenges.model.quest.QuestsDifficulty;

import java.util.UUID;

@Setter
@Getter
@Schema(description = "Request payload for creating a new quest")
public class CreateQuestRequestDto {

    @Schema(
            description = "Quest title",
            example = "Defeat the Shadow Dragon",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Length(max = 255)
    @NotBlank
    private String title;

    @Schema(
            description = "Detailed quest description",
            example = "Player must defeat the dragon located in the dark forest.",
            maxLength = 5000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Length(max = 5000)
    @NotNull
    private String description;

    @Schema(
            description = "Difficulty level of the quest",
            example = "HARD",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private QuestsDifficulty difficulty;

    @Schema(
            description = "Reward points granted after successful completion",
            example = "5",
            minimum = "0",
            maximum = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Min(0)
    @Max(10)
    @NotNull
    private Integer rewardPoints;

    @Schema(
            description = "Associated badge unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID badge;

    @Schema(
            description = "Maximum number of attempts allowed",
            example = "3",
            minimum = "1",
            maximum = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Min(1)
    @Max(5)
    @NotNull
    private Integer maxAttempts;
}
