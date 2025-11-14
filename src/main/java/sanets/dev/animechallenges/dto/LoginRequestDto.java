package sanets.dev.animechallenges.dto;

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

    @Length(min = 8)
    @NotNull
    private String password;
}
