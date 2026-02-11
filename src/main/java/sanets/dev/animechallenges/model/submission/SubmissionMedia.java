package sanets.dev.animechallenges.model.submission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.media.MediaType;

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
    @JoinColumn(name = "submission_uid", referencedColumnName = "uid", updatable = false)
    @NotNull
    private Submission submission;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", columnDefinition = "media_type", nullable = false)
    private MediaType mediaType;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_uid", referencedColumnName = "uid", nullable = false)
    @NotNull
    private Media media;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime createdAt;
}
