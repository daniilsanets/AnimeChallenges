package sanets.dev.animechallenges.dto.participation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.quest.QuestStatus;

@Getter
@Setter
public class UpdateQuestParticipationRequestDto {

    @NotNull
    @Schema(
            description = "New status of the quest participation",
            example = "SUBMITTED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private QuestStatus questStatus;

    @Min(0)
    @Schema(
            description = "New score for submission. Must be positive",
            example = "7",
            minimum = "0"
    )
    private Integer score;
}
