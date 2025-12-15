package sanets.dev.animechallenges.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileRequestDto {

    @Email
    private String email;

    @Max(100)
    private String username;

    @Max(200)
    private String nickname;

    @Max(1000)
    private String bio;
}
