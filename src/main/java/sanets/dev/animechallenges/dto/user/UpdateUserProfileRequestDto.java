package sanets.dev.animechallenges.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Max(100)
    private String username;

    @NotNull
    @Max(200)
    private String nickname;

    @NotNull
    @Max(1000)
    private String bio;
}
