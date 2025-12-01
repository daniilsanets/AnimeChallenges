package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sanets.dev.animechallenges.model.UserBadge;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {
    Optional<UserBadge> findByUserUidAndBadgeUid(UUID userUid, UUID badgeUid);

    @Query("SELECT ub.badge.uid FROM UserBadge ub WHERE ub.user.uid = :userId")
    Set<UUID> findBadgeIdsByUser(@Param("userId") UUID userId);

    @Modifying()
    @Query(value = "INSERT INTO user_badge(user_uid, badge_uid, awarded_at) VALUES (:userId, :badgeId, :awardedAt) ON CONFLICT (user_uid, badge_uid) DO NOTHING",
    nativeQuery = true)
    int insertUserBadge(@Param("userId") UUID userUid,
                         @Param("badgeId") UUID badgeUid,
                         @Param("awardedAt") OffsetDateTime awardedAt);

}
