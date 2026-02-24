package sanets.dev.animechallenges.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
public class SignUpRequestDto {

    @Length(max = 100)
    @NotNull
    private String username;

    @Length(max = 320)
    @Email
    @NotNull
    private String email;

    @Length(max = 200)
    @NotNull
    private String password;

}
