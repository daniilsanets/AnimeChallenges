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
@Table(name = "user_badge")
@IdClass(UserBadgeId.class)
public class UserBadge {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", referencedColumnName = "uid")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_uid", referencedColumnName = "uid")
    private Badge badge;

    @CreationTimestamp
    @Column(name = "awarded_at", updatable = false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime awardedAt;
}
