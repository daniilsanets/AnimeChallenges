package sanets.dev.animechallenges.model.quest;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lifecycle status of a user's quest participation")
public enum QuestStatus {

    @Schema(description = "Participation was created but the quest has not been started yet")
    PENDING,

    @Schema(description = "User has started working on the quest")
    STARTED,

    @Schema(description = "User submitted the quest for review")
    SUBMITTED,

    @Schema(description = "Submission was reviewed and approved")
    APPROVED,

    @Schema(description = "Submission was reviewed and rejected")
    REJECTED,

    @Schema(description = "Participation was cancelled by user or system")
    CANCELLED
}