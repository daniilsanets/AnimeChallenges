package sanets.dev.animechallenges.dto;

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
    @NotNull
    private String email;

    @Length(min = 8)
    @NotNull
    private String password;

}
