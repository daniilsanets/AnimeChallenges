package sanets.dev.animechallenges.controller.abstraction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;

import java.util.UUID;

@Tag(name = "Submissions", description = "Operations related to quest submissions")
@RequestMapping("/api/v1/submissions")
@SecurityRequirement(name = "bearerAuth")
public interface SubmissionController {
    @Operation(
            summary = "Create new quest submission",
            description = "Allows a user to submit quest solution data including files. "
                    + "Consumes multipart/form-data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Submission successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    SubmissionResponseDto createSubmission(
            @Parameter(description = "Submission creation payload", required = true)
            @Valid @ModelAttribute CreateSubmissionRequestDto dto
    );

    @Operation(
            summary = "Approve submission (ADMIN only)",
            description = "Marks a submission as approved. Accessible only to ADMIN users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission approved"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @PatchMapping("/admin/{uid}/approve")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    SubmissionResponseDto approveSubmission(
            @Parameter(description = "Submission unique identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true)
            @PathVariable UUID uid
    );

    @Operation(
            summary = "Reject submission (ADMIN only)",
            description = "Marks a submission as rejected. Accessible only to ADMIN users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission rejected"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @PatchMapping("/admin/{uid}/reject")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    SubmissionResponseDto rejectSubmission(
            @Parameter(description = "Submission unique identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true)
            @PathVariable UUID uid
    );

    @Operation(
            summary = "Get submission by UID (ADMIN only)",
            description = "Returns submission details by its unique identifier. Accessible only to ADMIN users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Submission retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "Submission not found")
    })
    @GetMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    SubmissionResponseDto getSubmission(
            @Parameter(description = "Submission unique identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true)
            @PathVariable UUID uid
    );
}
