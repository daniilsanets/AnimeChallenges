package sanets.dev.animechallenges.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.BadgeType;

import java.util.Map;

@Getter
@Setter
public class BadgeRequestDto {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private BadgeType badgeType;

    @NotNull
    private Map<String, Object> rule;
}
