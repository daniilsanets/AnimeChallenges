package sanets.dev.animechallenges.controller.abstraction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import sanets.dev.animechallenges.dto.quest.CreateQuestRequestDto;
import sanets.dev.animechallenges.dto.quest.QuestFilterDto;
import sanets.dev.animechallenges.dto.quest.QuestResponseDto;
import sanets.dev.animechallenges.dto.quest.UpdateQuestRequestDto;

import java.util.UUID;

@Tag(name = "Quest API", description = "Operations related to quests management")
@RequestMapping("/api/v1/quests")
@SecurityRequirement(name = "bearerAuth")
public interface QuestController {

    @Operation(
            summary = "Create quest",
            description = "Creates a new quest entity"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Quest successfully created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createQuest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Quest creation payload",
                    required = true
            )
            @RequestBody @Valid CreateQuestRequestDto dto
    );

    @Operation(
            summary = "Get quests",
            description = "Returns paginated list of quests with optional filtering"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list of quests",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = QuestResponseDto.class)
                    )
            )
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<QuestResponseDto> getQuests(
            @ParameterObject QuestFilterDto filterDto,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    );

    @Operation(
            summary = "Update quest",
            description = "Partially updates quest by UID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quest successfully updated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quest not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body"
            )
    })
    @PatchMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    void updateQuest(
            @Parameter(
                    description = "Quest unique identifier",
                    required = true
            )
            @PathVariable UUID uid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Quest update payload",
                    required = true
            )
            @RequestBody @Valid UpdateQuestRequestDto dto
    );

    @Operation(
            summary = "Delete quest",
            description = "Deletes quest by UID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Quest successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quest not found"
            )
    })
    @DeleteMapping("/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuest(
            @Parameter(
                    description = "Quest unique identifier",
                    required = true
            )
            @PathVariable UUID uid
    );
}
