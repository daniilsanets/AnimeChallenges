package sanets.dev.animechallenges.dto.participation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.quest.QuestStatus;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Response object representing a user's participation in a quest")
public class QuestParticipationResponseDto {
    @Schema(
            description = "Unique identifier of the participation",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uid;

    @Schema(
            description = "Unique identifier of the user participating in the quest",
            example = "660e8400-e29b-41d4-a716-446655440001"
    )
    private UUID userUid;

    @Schema(
            description = "Unique identifier of the quest",
            example = "770e8400-e29b-41d4-a716-446655440002"
    )
    private UUID questUid;

    @Schema(
            description = "Current status of the quest participation",
            example = "COMPLETED"
    )
    private QuestStatus questStatus;

    @Schema(
            description = "Score achieved by the user in this quest",
            example = "120",
            minimum = "0"
    )
    private Integer score;
}
