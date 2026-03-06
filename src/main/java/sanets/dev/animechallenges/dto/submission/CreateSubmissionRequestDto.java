package sanets.dev.animechallenges.dto.submission;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request object for creating a quest submission")
public class CreateSubmissionRequestDto {

    @NotNull
    @Schema(
            description = "Unique identifier of the quest participation",
            example = "660e8400-e29b-41d4-a716-446655440001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID participationUid;

    @NotNull
    @Schema(
            description = "Initial submission status",
            example = "SUBMITTED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private SubmissionStatus submissionStatus;

    @ArraySchema(
            schema = @Schema(
                    type = "string",
                    format = "binary",
                    description = "Files attached to the submission"
            )
    )
    private List<MultipartFile> multipartFiles;

    @NotNull
    @Length(max = 5000)
    @Schema(
            description = "Detailed description of the submission",
            example = "Implemented REST API and added validation layer.",
            maxLength = 5000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String description;

    @NotNull
    @Length(max = 1000)
    @Schema(
            description = "Additional notes related to submission",
            example = "Swagger documentation included.",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String notes;
}
