package sanets.dev.animechallenges.dto.quest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sanets.dev.animechallenges.model.QuestsDifficulty;

import java.util.UUID;

@Getter
@Setter
public class QuestRequestToUpdateDto {

    @Length(max = 5000)
    private String description;

    private QuestsDifficulty difficulty;

    @Min(0)
    @Max(10)
    private Integer rewardPoints;

    @Min(1)
    @Max(5)
    private Integer maxAttempts;
}
