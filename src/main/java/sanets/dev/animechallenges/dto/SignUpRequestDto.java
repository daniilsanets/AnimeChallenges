package sanets.dev.animechallenges.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignUpRequestDto {

    private String username;
    private String email;
    private String password;

}
