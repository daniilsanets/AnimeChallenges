package sanets.dev.animechallenges.dto.participation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(name = "Required data for creating participation")
public class CreateParticipationRequestDto {

    @NotNull
    @Schema(
            description = "Unique identifier of the quest to participate in",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID questUid;

    @Min(0)
    @NotNull
    @Schema(
            description = "Score achieved in the quest. Must be greater than or equal to 0",
            example = "100",
            minimum = "0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer score;
}
