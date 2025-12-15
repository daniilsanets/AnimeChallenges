package sanets.dev.animechallenges.dto.user;

import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.UserRole;

import java.util.UUID;

@Getter
@Setter
public class UserProfileResponseDto {
    private String email;
    private String username;
    private UserRole role;
    private String nickname;
    private String bio;
    private UUID avatarUid;
    private Boolean isActive;
}
