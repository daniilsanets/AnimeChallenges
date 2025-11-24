package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.BadgeType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, UUID> {
    Optional<Badge> findBadgeByUid(UUID uid);

    List<Badge> findByBadgeType(BadgeType badgeType);

    @Query("SELECT b FROM Badge b LEFT JOIN FETCH b.image WHERE b.isActive = true")
    List<Badge> findAllWithImages();

    @Query("""
    SELECT b 
    FROM Badge b 
    JOIN UserBadge ub ON ub.badge = b 
    LEFT JOIN FETCH b.image 
    WHERE ub.user.uid = :userId
""")
    List<Badge> findAllBadgesByUserId(@Param("userId") UUID userId);
}
