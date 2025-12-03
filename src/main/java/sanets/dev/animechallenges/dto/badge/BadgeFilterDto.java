package sanets.dev.animechallenges.dto.badge;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadgeFilterDto {
    private String nameQuery;
    private Boolean isActive;
}
