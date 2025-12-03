package sanets.dev.animechallenges.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class LoginRequestDto {

    @Length(max = 320)
    @NotNull
    private String usernameOrEmail;

    @Length(max = 200)
    @NotNull
    private String password;
}
