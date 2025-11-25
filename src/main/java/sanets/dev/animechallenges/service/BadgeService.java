package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.BadgeRequestDto;
import sanets.dev.animechallenges.dto.BadgeResponseDto;
import sanets.dev.animechallenges.exception.BadgeNotFoundException;
import sanets.dev.animechallenges.exception.MediaNotUploadedException;
import sanets.dev.animechallenges.mapper.BadgeMapper;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.BadgeType;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.model.Quest;
import sanets.dev.animechallenges.model.QuestStatus;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.BadgeRepository;
import sanets.dev.animechallenges.repository.QuestParticipationRepository;
import sanets.dev.animechallenges.repository.UserBadgeRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private static final String BADGE_NOT_FOUND_MSG = "Badge not found";
    private static final String BADGE_NOT_SAVED_MSG = "Failed to save badge";

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final QuestParticipationRepository questParticipationRepository;
    private final BadgeMapper badgeMapper;
    private final MediaService mediaService;

    public boolean userHasBadge(User user, Badge badge) {
        return userBadgeRepository.findByUser_Uid_AndBadge_Uid(user.getUid(), badge.getUid()).isPresent();
    }

    private boolean saveBadgeToUser(User user, Badge badge) {
        return saveBadgeToUser(user, badge, false);
    }

    public boolean saveBadgeToUser(User user, Badge badge, boolean forced) {
        if (userHasBadge(user, badge) && !forced) {
            return false;
        }

        return userBadgeRepository.insertUserBadge(user.getUid(), badge.getUid(), OffsetDateTime.now()) == 1;
    }

    public boolean tryAssignAchievement(User user, Badge badge, Long currentCount) {
        Object countRule = badge.getRule().get("count");

        if (countRule == null || !(currentCount >= ((Number) countRule).longValue())) {
            return false;
        }
        saveBadgeToUser(user, badge, true);
        return true;
    }

    public void tryAssignQuestReward(User user, Badge badge) {
        saveBadgeToUser(user, badge);
    }

    @Transactional
    public void processQuestCompletion(User user, Quest quest){

        Long numberOfCompletedQuests = questParticipationRepository.countByPerformerAndQuestStatus(user, QuestStatus.APPROVED);

        Set<UUID> ownedBadgeIds = userBadgeRepository.findBadgeIdsByUser(user.getUid());

        List<Badge> achievements = badgeRepository.findByBadgeType(BadgeType.ACHIEVEMENT);

        for (Badge badge : achievements) {
            if(!ownedBadgeIds.contains(badge.getUid()) && tryAssignAchievement(user, badge, numberOfCompletedQuests)) {
                ownedBadgeIds.add(badge.getUid());
            }
        }
        Badge questBadge = quest.getBadge();
        if (questBadge != null && !ownedBadgeIds.contains(questBadge.getUid())) {
            saveBadgeToUser(user, questBadge, true);
        }
    }

    @Transactional
    public void createBadge(BadgeRequestDto  badgeRequestDto, MultipartFile file) throws MediaNotUploadedException {
        Media media = mediaService.upload(file);

        String code = UUID.randomUUID().toString();

        Badge badge = badgeMapper.toBadge(badgeRequestDto, media, code);

        try {
            badgeRepository.save(badge);
        } catch (DataAccessException ex) {
            mediaService.deleteFileOnly(media.getStorageKey());
            String msg = BADGE_NOT_SAVED_MSG + ex.getMessage();
            throw new MediaNotUploadedException(msg);
        }
    }

    public List<BadgeResponseDto> getAllActiveBadges() {
        List<Badge> badges = badgeRepository.findAllWithImages();

        return badges.stream()
                .map(badgeMapper::toBadgeResponseDto)
                .toList();
    }

    @Transactional
    public void deleteBadge(UUID badgeUid) throws  BadgeNotFoundException {
        Badge badge = badgeRepository.findBadgeByUid(badgeUid)
                .orElseThrow(() -> new BadgeNotFoundException(BADGE_NOT_FOUND_MSG));

        badge.setActive(false);

        badgeRepository.save(badge);
    }

    public List<BadgeResponseDto> getUserBadges(UUID userId) {
        return badgeRepository.findAllBadgesByUserId(userId).stream()
                .map(badgeMapper::toBadgeResponseDto)
                .toList();
    }
}
