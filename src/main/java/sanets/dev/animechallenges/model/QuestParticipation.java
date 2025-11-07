package sanets.dev.animechallenges.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
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
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", referencedColumnName = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private User performer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_uid", referencedColumnName = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Quest quest;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_status", columnDefinition = "", nullable = false)
    private QuestStatus questStatus;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    //Может быть updatable = false => убрать сеттер
    @Min(1)
    @Column(name = "score", nullable = false)
    private Integer score;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

}
