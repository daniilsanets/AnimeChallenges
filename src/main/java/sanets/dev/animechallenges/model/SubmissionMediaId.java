package sanets.dev.animechallenges.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubmissionMediaId implements Serializable {
    private UUID submission;
    private UUID media;
}
