package sanets.dev.animechallenges.dto.participation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.quest.QuestStatus;

@Getter
@Setter
public class UpdateQuestParticipationRequestDto {

    @NotNull
    private QuestStatus questStatus;

    @Min(0)
    private Integer score;
}
