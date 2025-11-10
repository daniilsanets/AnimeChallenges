package sanets.dev.animechallenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "quest_participation")
public class QuestParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", referencedColumnName = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private User performer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_uid", referencedColumnName = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private Quest quest;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_status", columnDefinition = "quests_status", nullable = false)
    @NotNull
    private QuestStatus questStatus;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Min(0)
    @Column(name = "score", nullable = false)
    @NotNull
    private Integer score;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}
