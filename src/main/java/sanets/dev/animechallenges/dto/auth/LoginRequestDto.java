package sanets.dev.animechallenges.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(name = "Login Payload")
public class LoginRequestDto {

    @Schema(
            description = "Username or email",
            examples = {
                    "Danechka",
                    "danechka@gmail.com"
            }
    )
    @Length(max = 320)
    @NotNull
    private String usernameOrEmail;

    @Schema(
            description = "Password",
            example = "StrongPassword"
    )
    @Length(max = 200)
    @NotNull
    private String password;
}
