package sanets.dev.animechallenges.dto.quest;

import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.quest.QuestsDifficulty;

@Getter
@Setter
public class QuestFilterDto {
    private String title;
    private QuestsDifficulty difficulty;
    private Integer rewardPoints;
    private Integer maxAttempts;
    private boolean active;
}
