package sanets.dev.animechallenges.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SignUpResponseDto {
    String accessToken;
    String refreshToken;
}
