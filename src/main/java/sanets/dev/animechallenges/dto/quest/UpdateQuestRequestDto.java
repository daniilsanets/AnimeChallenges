package sanets.dev.animechallenges.dto.quest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sanets.dev.animechallenges.model.quest.QuestsDifficulty;

@Getter
@Setter
@Schema(description = "Request payload for partial quest update")public class UpdateQuestRequestDto {

    @Schema(
            description = "Updated quest description",
            maxLength = 5000,
            example = "Updated quest description"
    )
    @Length(max = 5000)
    private String description;

    @Schema(
            description = "Updated difficulty level",
            example = "MEDIUM"
    )
    private QuestsDifficulty difficulty;

    @Schema(
            description = "Updated reward points",
            minimum = "0",
            maximum = "10",
            example = "7"
    )
    @Min(0)
    @Max(10)
    private Integer rewardPoints;

    @Schema(
            description = "Updated maximum attempts",
            minimum = "1",
            maximum = "5",
            example = "4"
    )
    @Min(1)
    @Max(5)
    private Integer maxAttempts;
}
