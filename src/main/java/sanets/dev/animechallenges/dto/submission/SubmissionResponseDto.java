package sanets.dev.animechallenges.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.submission.SubmissionStatus;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Response object representing a quest submission")
public class SubmissionResponseDto {

    @Schema(
            description = "Unique identifier of the submission",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID uid;

    @Schema(
            description = "Unique identifier of the related quest participation",
            example = "660e8400-e29b-41d4-a716-446655440001"
    )
    private UUID participationUid;

    @Schema(
            description = "Main submission description provided by the user",
            example = "Implemented full authentication flow with JWT."
    )
    private String description;

    @Schema(
            description = "Additional notes provided by the user",
            example = "All unit tests are passing."
    )
    private String notes;

    @Schema(
            description = "Current status of the submission",
            example = "SUBMITTED"
    )
    private SubmissionStatus submissionStatus;
}
