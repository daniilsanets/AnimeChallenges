package sanets.dev.animechallenges.dto.submission;

import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.submission.SubmissionStatus;

import java.util.UUID;

@Getter
@Setter
public class SubmissionResponseDto {
    private UUID uid;
    private UUID participationUid;
    private String description;
    private String notes;
    private SubmissionStatus submissionStatus;
}
