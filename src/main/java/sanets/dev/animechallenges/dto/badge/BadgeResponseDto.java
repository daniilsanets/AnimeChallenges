package sanets.dev.animechallenges.dto.badge;

import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.BadgeType;

import java.util.UUID;

@Getter
@Setter
public class BadgeResponseDto {
    private UUID uid;
    private String title;
    private String description;
    private BadgeType badgeType;
    private String imageUrl;
}
