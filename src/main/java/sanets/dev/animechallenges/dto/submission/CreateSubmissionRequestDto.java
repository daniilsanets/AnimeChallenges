package sanets.dev.animechallenges.dto.submission;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.model.submission.SubmissionStatus;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateSubmissionRequestDto {
    @NotNull
    private UUID participationUid;

    @NotNull
    private SubmissionStatus submissionStatus;

    private List<MultipartFile> multipartFiles;

    @NotNull
    @Length(max = 5000)
    private String description;

    @NotNull
    @Length(max = 1000)
    private String notes;
}
