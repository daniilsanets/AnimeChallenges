package sanets.dev.animechallenges.dto.submission;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateSubmissionResponseDto {
    private UUID uid;
    private UUID participationUid;
    private String description;
    private String notes;
}
