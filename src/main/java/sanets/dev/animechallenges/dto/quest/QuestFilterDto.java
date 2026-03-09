package sanets.dev.animechallenges.dto.quest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.quest.QuestsDifficulty;

@Getter
@Setter
@Schema(description = "Filtering parameters for quest search")
public class QuestFilterDto {
    @Schema(
            description = "Filter by title (partial match)",
            example = "Dragon"
    )
    private String title;

    @Schema(
            description = "Filter by difficulty level",
            example = "MEDIUM"
    )
    private QuestsDifficulty difficulty;

    @Schema(
            description = "Filter by exact reward points",
            example = "5"
    )
    private Integer rewardPoints;

    @Schema(
            description = "Filter by maximum attempts",
            example = "3"
    )
    private Integer maxAttempts;

    @Schema(
            description = "Filter by active status",
            example = "true"
    )
    private boolean active;
}
