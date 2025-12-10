package sanets.dev.animechallenges.dto.quest;

import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.QuestsDifficulty;

import java.util.UUID;

@Getter
@Setter
public class QuestResponseDto {
    private UUID uid;
    private String title;
    private String description;
    private QuestsDifficulty difficulty;
    private Integer rewardPoints;
    private UUID badgeUid;
    private Integer maxAttempts;
    private UUID creatorUid;
    private Boolean isActive;
}
