package sanets.dev.animechallenges.controller.abstraction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import sanets.dev.animechallenges.dto.participation.CreateParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.QuestParticipationResponseDto;
import sanets.dev.animechallenges.dto.participation.UpdateQuestParticipationRequestDto;

import java.util.UUID;

@Tag(
        name = "Quest Participation",
        description = "Operations related to user participation in quests"
)
@RequestMapping("/api/v1/participations")
@SecurityRequirement(name = "bearerAuth")
public interface QuestParticipationController {

    @Operation(
            summary = "Create quest participation",
            description = "Creates a new participation entry for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Participation successfully created",
                    content = @Content(
                            schema = @Schema(implementation = QuestParticipationResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    QuestParticipationResponseDto createParticipation(
            @Parameter(description = "Participation creation payload", required = true)
            @RequestBody CreateParticipationRequestDto createParticipationRequestDto
    );

    @Operation(
            summary = "Get participation by UID",
            description = "Returns quest participation details by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participation found",
                    content = @Content(
                            schema = @Schema(implementation = QuestParticipationResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Participation not found")
    })
    @GetMapping("/{uid}")
    QuestParticipationResponseDto getParticipationByUid(
            @Parameter(description = "Participation UUID ", required = true)
            @PathVariable UUID uid
    );

    @Operation(
            summary = "Get all participations by user (Admin only)",
            description = "Returns paginated participations for a specific user. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of participations"
            ),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/admin/users/{userUid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    Page<QuestParticipationResponseDto> getAllParticipationsByUserUid(
            @Parameter(description = "Participation UUID", required = true)
            @PathVariable UUID userUid,

            @Parameter(description = "Pagination parameters: page, size, sort")
            @PageableDefault(20) Pageable pageable
    );

    @Operation(
            summary = "Update participation by uid (Admin only)",
            description = "Updating quest participation with dto had fields. Requires Admin role"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Participation successfully updated",
                    content = @Content(
                            schema = @Schema(implementation = QuestParticipationResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Participation not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    QuestParticipationResponseDto updateQuestParticipationByUid(
            @Parameter(description = "Participation unique identifier", required = true)
            @PathVariable
            UUID uid,

            @Parameter(description = "Update payload", required = true)
            @RequestBody
            UpdateQuestParticipationRequestDto dto);

    @Operation(
            summary = "Cancel participation (Admin only)",
            description = "Deletes or cancels quest participation by UID. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Participation successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Participation not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void cancelParticipationByUid(
            @Parameter(name = "Participation UUID ")
            @PathVariable UUID uid
    );
}
