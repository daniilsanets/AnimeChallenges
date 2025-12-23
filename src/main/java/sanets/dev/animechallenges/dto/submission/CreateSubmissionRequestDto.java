package sanets.dev.animechallenges.dto.submission;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import sanets.dev.animechallenges.model.SubmissionType;

import java.util.UUID;

@Getter
@Setter
public class CreateSubmissionRequestDto {
    @NotNull
    private UUID participationUid;

    @NotNull
    private SubmissionType submissionType;

    @NotNull
    @Length(max = 5000)
    private String description;

    @NotNull
    @Length(max = 1000)
    private String notes;
}
