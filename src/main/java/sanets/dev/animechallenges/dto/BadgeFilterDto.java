package sanets.dev.animechallenges.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadgeFilterDto {
    private String nameQuery;
    private Boolean isActive;
}
