package sanets.dev.animechallenges.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Schema(description = "Sign Up request payload")
public class SignUpRequestDto {

    @Schema(
            description = "Username",
            example = "Danechka"
    )
    @Length(max = 100)
    @NotNull
    private String username;

    @Schema(
            description = "Email",
            example = "danechka@gmail.com"
    )
    @Length(max = 320)
    @Email
    @NotNull
    private String email;

    @Schema(
            description = "User password",
            example = "StrongPassword"
    )
    @Length(max = 200)
    @NotNull
    private String password;

}
