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
import jakarta.validation.constraints.Max;
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
import org.hibernate.validator.constraints.Length;

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
    @NotNull
    private UUID uid;

    @Column(name = "title", nullable = false, length = 255)
    @NotNull
    @Length(max = 255)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @NotNull
    @Length(max = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, columnDefinition = "quests_difficulty")
    @NotNull
    private QuestsDifficulty difficulty;

    @Min(0)
    @Max(10)
    @Column(name = "reward_points", nullable = false)
    @NotNull
    private Integer rewardPoints;

    @Min(1)
    @Max(5)
    @Column(name = "max_attempts", nullable = false)
    @NotNull
    private Integer maxAttempts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_uid", referencedColumnName = "uid", nullable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private User creator;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private OffsetDateTime updatedAt;

}
