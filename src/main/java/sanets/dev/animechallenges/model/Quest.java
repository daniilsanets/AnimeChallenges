package sanets.dev.animechallenges.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "quest")
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid",  updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID uid;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, columnDefinition = "quests_difficulty")
    private QuestsDifficulty difficulty;

    @Min(0)
    @Max(10)
    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints = 0;

    @Min(1)
    @Max(5)
    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "creator_uid", referencedColumnName = "uid")
    @Setter(AccessLevel.NONE)
    private User creator;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
