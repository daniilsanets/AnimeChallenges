package sanets.dev.animechallenges.dto.badge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sanets.dev.animechallenges.model.badge.BadgeType;

import java.util.Map;

@Getter
@Setter
@Schema(
        name = "BadgeRequestDto",
        description = "Request payload for creating or updating a badge. Contains badge metadata and rule definition."
)
public class BadgeRequestDto {

    @NotBlank
    @Schema(
            description = "Unique badge title. Must not be blank.",
            example = "Java Master",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;

    @Schema(
            description = "Optional detailed description of the badge and its purpose.",
            example = "Awarded for completing all advanced Java challenges."
    )
    private String description;

    @NotNull
    @Schema(
            description = "Type of badge that defines how the rule should be interpreted.",
            example = "ACHIEVEMENT",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BadgeType badgeType;

    @NotNull
    @Schema(
            description = "Rule definition in JSON format. Structure depends on badgeType. " +
                    "Contains parameters required to evaluate badge eligibility.",
            example = """
                      {
                        "count": 5,
                        "requiredPoints": 1000,
                        "requiredLevel": 5,
                        "category": "Fantasy-ludaman"
                      }
                      """,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Map<String, Object> rule;
}
