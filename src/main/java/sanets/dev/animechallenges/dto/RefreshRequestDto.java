package sanets.dev.animechallenges.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class RefreshRequestDto {

    @Length(max = 255)
    @NotNull
    private String refreshToken;

}
