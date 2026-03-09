package sanets.dev.animechallenges.controller.abstraction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.media.MediaResponseDto;

import java.util.UUID;

@Tag(name = "Media Controller" , description = "Operations for managing media files")
@RequestMapping("/api/v1/media")
@SecurityRequirement(name = "bearerAuth")
public interface MediaController {

    @Operation(
            summary = "Upload media file",
            description = "Creates new media entity and uploads file to storage"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Media successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MediaResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    MediaResponseDto createMedia(
            @Parameter(
                    description = "File to upload",
                    required = true
            )
            @RequestPart("file") MultipartFile file
    );

    @Operation(
            summary = "Get media by UID",
            description = "Returns media metadata by unique identifier (ADMIN only)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Media found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MediaResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Media not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @GetMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    MediaResponseDto getMediaByUid(@PathVariable("uid") UUID mediaUid);

    @Operation(
            summary = "Delete media by UID",
            description = "Deletes media by unique identifier (ADMIN only)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Media successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Media not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @DeleteMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteMediaByUid(@PathVariable("uid") UUID submissionUid);
}
