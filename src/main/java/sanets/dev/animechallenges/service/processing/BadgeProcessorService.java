package sanets.dev.animechallenges.service.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanets.dev.animechallenges.model.badge.Badge;
import sanets.dev.animechallenges.model.badge.BadgeType;
import sanets.dev.animechallenges.model.quest.Quest;
import sanets.dev.animechallenges.model.quest.QuestStatus;
import sanets.dev.animechallenges.repository.BadgeRepository;
import sanets.dev.animechallenges.repository.UserBadgeRepository;
import sanets.dev.animechallenges.service.BadgeService;
import sanets.dev.animechallenges.service.QuestParticipationService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BadgeProcessorService {

    private final QuestParticipationService questParticipationService;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeService badgeService;

    @Transactional
    public void processQuestCompletion(UUID userUid, Quest quest) {
        log.info("Process quest completion for user {}", userUid);

        long completedCount = questParticipationService.getCountByPerformerAndQuestStatus(
                                userUid,
                                QuestStatus.APPROVED
                        );

        assignAchievementBadges(userUid, completedCount);
        assignQuestRewardBadge(userUid, quest);
    }

    private void assignAchievementBadges(UUID userUid, Long completedCount) {
        Set<UUID> ownedBadgeIds = userBadgeRepository.findBadgeIdsByUser(userUid);
        List<Badge> achievements =
                badgeRepository.findByBadgeType(BadgeType.ACHIEVEMENT);

        for (Badge badge : achievements) {
            if (ownedBadgeIds.contains(badge.getUid())) {
                continue;
            }

            Long required = extractRequiredCount(badge);

            if (completedCount >= required) {
                badgeService.saveBadgeToUser(userUid, badge, true);
                ownedBadgeIds.add(badge.getUid());
            }
        }
    }

    private void assignQuestRewardBadge(UUID userUid, Quest quest) {
        Badge reward = quest.getBadge();
        if (reward == null) {
            return;
        }

        boolean alreadyOwned =
                userBadgeRepository.existsByUserUidAndBadgeUid(userUid, reward.getUid());

        if (!alreadyOwned) {
            badgeService.saveBadgeToUser(userUid, reward, true);
        }
    }

    private long extractRequiredCount(Badge badge) {
        Object value = badge.getRule().get("count");

        if (!(value instanceof Number number)) {
            throw new IllegalStateException("Badge " + badge.getUid() + " has invalid count rule");
        }

        return number.longValue();
    }
}
