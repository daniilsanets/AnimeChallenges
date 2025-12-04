package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.badge.BadgeFilterDto;
import sanets.dev.animechallenges.dto.badge.BadgeRequestDto;
import sanets.dev.animechallenges.dto.badge.BadgeResponseDto;
import sanets.dev.animechallenges.exception.badge.BadgeNotFoundException;
import sanets.dev.animechallenges.exception.media.MediaNotUploadedException;
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

import static sanets.dev.animechallenges.repository.specification.BadgeSpecification.isActive;
import static sanets.dev.animechallenges.repository.specification.BadgeSpecification.nameContains;

@Slf4j
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
        log.debug("Check has user {} badge {}", user.getUid(), badge);
        return userBadgeRepository.findByUserUidAndBadgeUid(user.getUid(), badge.getUid()).isPresent();
    }

    private boolean saveBadgeToUser(User user, Badge badge) {
        return saveBadgeToUser(user, badge, false);
    }

    public boolean saveBadgeToUser(User user, Badge badge, boolean forced) {
        if (userHasBadge(user, badge) && !forced) {
            log.debug("User {} badge {} not saved because it has badge", user.getUid(), badge);
            return false;
        }

        log.info("Save badge to user{}", user.getUid());
        return userBadgeRepository.insertUserBadge(user.getUid(), badge.getUid(), OffsetDateTime.now()) == 1;
    }

    public boolean tryAssignAchievement(User user, Badge badge, Long currentCount) {
        Object countRule = badge.getRule().get("count");

        if (countRule == null || !(currentCount >= ((Number) countRule).longValue())) {
            log.debug("User {} cannot get achievement {} due to it has it or countRule is null", user.getUid(), badge.getUid());
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

        log.debug("Trying to get completed quest number to user {}", user.getUid());
        Long numberOfCompletedQuests = questParticipationRepository.countByPerformerAndQuestStatus(user, QuestStatus.APPROVED);

        Set<UUID> ownedBadgeIds = userBadgeRepository.findBadgeIdsByUser(user.getUid());

        List<Badge> achievements = badgeRepository.findByBadgeType(BadgeType.ACHIEVEMENT);

        log.debug("Adding badge filter");
        achievements.stream()
                .filter(badge -> !ownedBadgeIds.contains(badge.getUid()))
                .forEach(badge -> {
                    if (tryAssignAchievement(user, badge, numberOfCompletedQuests)) {
                        ownedBadgeIds.add(badge.getUid());
                        log.debug("User badge uid was added to its owned list of badges");
                    }
                });

        Badge questBadge = quest.getBadge();
        if (questBadge != null && !ownedBadgeIds.contains(questBadge.getUid())) {
            saveBadgeToUser(user, questBadge, true);
            log.info("User {} take its reward", user.getUid());
        }
    }

    @Transactional
    public void createBadge(BadgeRequestDto  badgeRequestDto, MultipartFile file) throws MediaNotUploadedException {
        log.debug("Upload file (badge picture) {} to media db", file.getOriginalFilename());
        Media media = mediaService.upload(file);

        String code = UUID.randomUUID().toString();

        log.debug("Will map badge request dto to badge, code {}", code);
        Badge badge = badgeMapper.toBadge(badgeRequestDto, media, code);

        try {
            badgeRepository.save(badge);
            log.info("Saved badge to db {}", badge);
        } catch (DataAccessException ex) {
            mediaService.deleteFileOnly(media.getStorageKey());
            String msg = BADGE_NOT_SAVED_MSG + ex.getMessage();
            log.error("Couldn't save badge exception {} ", ex.getMessage());
            throw new MediaNotUploadedException(msg);
        }
    }

    public Page<BadgeResponseDto> getBadgesWithFilter(BadgeFilterDto filter, Pageable pageable) {
        Specification<Badge> spec = Specification.where(null);

        spec = spec.and(nameContains(filter.getNameQuery()))
                .and(isActive(filter.getIsActive()));

        Page<Badge> badgePage = badgeRepository.findAll(spec, pageable);

        return badgePage.map(badgeMapper::toBadgeResponseDto);
    }

    public void deleteBadge(UUID badgeUid) throws BadgeNotFoundException {
        log.info("Delete badge {}", badgeUid);
        Badge badge = badgeRepository.findBadgeByUid(badgeUid)
                .orElseThrow(() -> new BadgeNotFoundException(BADGE_NOT_FOUND_MSG));
        log.debug("Find badge by uid {}", badgeUid);
        badge.setActive(false);
        badgeRepository.save(badge);
        log.info("Badge status was set as not active {}", badgeUid);
    }

    public List<BadgeResponseDto> getUserBadges(UUID userId) {
        log.info("Get user badges {}", userId);
        return badgeRepository.findAllBadgesByUserId(userId).stream()
                .map(badgeMapper::toBadgeResponseDto)
                .toList();
    }
}
