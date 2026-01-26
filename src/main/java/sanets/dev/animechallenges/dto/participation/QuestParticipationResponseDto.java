package sanets.dev.animechallenges.dto.participation;

import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.quest.QuestStatus;

import java.util.UUID;

@Getter
@Setter
public class QuestParticipationResponseDto {
    private UUID uid;
    private UUID userUid;
    private UUID questUid;
    private QuestStatus questStatus;
    private Integer score;
}
