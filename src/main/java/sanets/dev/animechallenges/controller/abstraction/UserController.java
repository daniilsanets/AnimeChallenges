package sanets.dev.animechallenges.controller.abstraction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import sanets.dev.animechallenges.dto.user.AdminUserProfileResponseDto;
import sanets.dev.animechallenges.dto.user.UpdateUserProfileRequestDto;
import sanets.dev.animechallenges.dto.user.UserProfileResponseDto;

import java.util.UUID;
@Tag(name = "User Profiles", description = "Management users profile, crud operations on them")
@RequestMapping("/api/v1/profiles")
@SecurityRequirement(name = "bearerAuth")
public interface UserController {
    @Operation(
            summary = "Get current authenticated user profile",
            description = "Returns profile information of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    UserProfileResponseDto getCurrentUserProfile();

    @Operation(
            summary = "Get user profile by UID",
            description = "Returns public profile information of a user by their unique identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{uid}")
    @ResponseStatus(HttpStatus.OK)
    UserProfileResponseDto getUserProfileByUid(
            @Parameter(
                    description = "Unique identifier of the user",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID uid
    );

    @Operation(
            summary = "Get full user profile (ADMIN only)",
            description = "Returns extended profile information for administrative purposes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin profile retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/admin/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    AdminUserProfileResponseDto getAdminUserProfileByUid(
            @Parameter(
                    description = "Unique identifier of the user",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID uid
    );

    @Operation(
            summary = "Update current user profile",
            description = "Updates profile information of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping()
    @ResponseStatus(HttpStatus.OK)
    UserProfileResponseDto updateProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User profile update payload",
                    required = true
            )
            @RequestBody UpdateUserProfileRequestDto updateDto
    );

    @Operation(
            summary = "Delete user (ADMIN only)",
            description = "Deletes a user by their unique identifier. Accessible only to ADMIN users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/admin/{uid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(
            @Parameter(
                    description = "Unique identifier of the user",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable UUID uid
    );
}
