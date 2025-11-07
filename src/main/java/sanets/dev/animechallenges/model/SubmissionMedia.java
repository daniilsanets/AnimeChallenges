package sanets.dev.animechallenges.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "submission_media")
@IdClass(SubmissionMediaId.class)
public class SubmissionMedia {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_uid", referencedColumnName = "uid")
    private Submission submission;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_uid", referencedColumnName = "uid")
    private Media media;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime created_at;
}
