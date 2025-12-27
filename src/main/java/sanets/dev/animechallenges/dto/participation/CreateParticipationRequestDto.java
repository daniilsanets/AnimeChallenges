package sanets.dev.animechallenges.dto.participation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateParticipationRequestDto {

    @NotNull
    private UUID questUid;

    @Min(0)
    @NotNull
    private Integer score;
}
