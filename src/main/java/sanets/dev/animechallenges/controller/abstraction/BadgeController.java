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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.badge.BadgeFilterDto;
import sanets.dev.animechallenges.dto.badge.BadgeRequestDto;
import sanets.dev.animechallenges.dto.badge.BadgeResponseDto;

import java.util.List;
import java.util.UUID;

@Tag(name = "Badge Controller", description = "Badge management")
@RequestMapping("/api/v1/badges")
@SecurityRequirement(name = "bearerAuth")
public interface BadgeController {
    @Operation(
            summary = "Returns all badges with filter"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully get filter badges"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data"
            )
    })
    @PostMapping
    Page<BadgeResponseDto> getBadges(BadgeFilterDto filter, @ParameterObject Pageable pageable);

    @GetMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get badge by UID",
            description = "Returns badge details by its unique identifier. Accessible only to users with ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Badge successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadgeResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Badge not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Admin role required."
            )
    })
    BadgeResponseDto getBadgeByUid(
            @Parameter(
                    description = "Badge unique identifier",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID uid);

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create new badge with image",
            description = "Creates a badge using JSON metadata and an image file. " +
                    "Request must be sent as multipart/form-data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Badge successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    void createBadge(
            @Parameter(
                    description = "Badge metadata in JSON format",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadgeRequestDto.class)
                    )
            )
            @RequestPart("badge") @Valid BadgeRequestDto badgeRequestDto,
            @Parameter(
                    description = "Image file associated with the badge",
                    required = true,
                    content = @Content(
                            mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
            @RequestPart("file") MultipartFile file
    );

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Archive badge",
            description = "Archives (soft deletes) a badge by its unique identifier. Accessible only to ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Badge successfully archived"),
            @ApiResponse(responseCode = "404", description = "Badge not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    void archiveBadge(
            @Parameter(
                    description = "Unique identifier of the badge to archive",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID uid);

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get user badges",
            description = "Returns list of badges assigned to a specific user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User badges successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadgeResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    List<BadgeResponseDto> getUserBadges(
            @Parameter(
                    description = "Unique identifier of the user",
                    required = true,
                    example = "9a1f6c80-7c3d-4e2b-bc4a-7b1fbbf6b111",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable UUID userId);

    //todo: id paramets take from path
    //ex:/assign/users/{userUid}/badges/{badgeUid}
    @PostMapping("/assign")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Assign badge to user",
            description = "Assigns a badge to a user. Accessible only to ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Badge assignment result",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "boolean")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "User or badge not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    Boolean assignBadgeToUser(
            @Parameter(
                    description = "Unique identifier of the user",
                    required = true,
                    example = "9a1f6c80-7c3d-4e2b-bc4a-7b1fbbf6b111",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @RequestParam UUID userUid,
            @Parameter(
                    description = "Unique identifier of the badge",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @RequestParam UUID badgeUid);
}
