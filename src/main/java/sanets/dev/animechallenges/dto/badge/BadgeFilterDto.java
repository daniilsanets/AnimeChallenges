package sanets.dev.animechallenges.dto.badge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Badge filter Object")
public class BadgeFilterDto {
    @Schema(
            description = "Part of badge nmame",
            example = "Naruto"
    )
    private String nameQuery;

    @Schema(
            description = "Is Badge Active to take",
            example = "true"
    )
    private Boolean isActive;
}
