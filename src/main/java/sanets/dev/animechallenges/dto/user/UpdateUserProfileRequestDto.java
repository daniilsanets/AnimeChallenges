package sanets.dev.animechallenges.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Request object for updating user profile")
public class UpdateUserProfileRequestDto {

    @Email
    @Schema(
            description = "New email of the user",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String email;

    @Size(max = 100)
    @Schema(
            description = "New username",
            example = "superuser123",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String username;

    @Size(max = 200)
    @Schema(
            description = "New nickname of the user",
            example = "Super Nick",
            maxLength = 200,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String nickname;

    @Size(max = 1000)
    @Schema(
            description = "Short biography of the user",
            example = "I am a passionate backend developer learning Spring Boot and OpenAPI.",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String bio;

    @Schema(
            description = "UUID of the new avatar image",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UUID avatarUid;
}
