package sanets.dev.animechallenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private UUID uid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_uid", referencedColumnName = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private QuestParticipation questParticipation;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", nullable = false, columnDefinition = "submission_status")
    @NotNull
    private SubmissionStatus submissionStatus;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT", length = 5000)
    @NotNull
    @Length(max = 5000)
    private String description;

    @Column(name = "notes", nullable = false, columnDefinition = "TEXT", length = 1000)
    @NotNull
    @Length(max = 1000)
    private String notes;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "rejected_at")
    private OffsetDateTime rejectedAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}
