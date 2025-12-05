package sanets.dev.animechallenges.dto.quest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sanets.dev.animechallenges.model.QuestsDifficulty;

import java.util.UUID;

@Setter
@Getter
public class CreateQuestRequestDto {

    @Length(max = 255)
    @NotBlank
    @Length
    private String title;

    @Length(max = 5000)
    @NotNull
    private String description;

    @NotNull
    private QuestsDifficulty difficulty;

    @Min(0)
    @Max(10)
    @NotNull
    private Integer rewardPoints;

    @NotNull
    private UUID badge;

    @Min(1)
    @Max(5)
    @NotNull
    private Integer maxAttempts;
}
