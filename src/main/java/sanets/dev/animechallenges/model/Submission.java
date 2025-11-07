package sanets.dev.animechallenges.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    private UUID uid;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_uid", referencedColumnName = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private QuestParticipation questParticipation;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", nullable = false, columnDefinition = "submission_status")
    private SubmissionStatus submissionStatus;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "notes", nullable = false)
    private String notes;

    @Column(name = "submitted_at", nullable = true)
    private OffsetDateTime submittedAt;

    @Column(name = "rejected_at", nullable = true)
    private OffsetDateTime rejectedAt;

    @Column(name = "approved_at", nullable = true)
    private OffsetDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}
