package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.quest.QuestResponseDto;
import sanets.dev.animechallenges.dto.quest.QuestFilterDto;
import sanets.dev.animechallenges.dto.quest.CreateQuestRequestDto;
import sanets.dev.animechallenges.dto.quest.UpdateQuestRequestDto;
import sanets.dev.animechallenges.exception.badge.BadgeNotFoundException;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.exception.quest.QuestNotFoundException;
import sanets.dev.animechallenges.exception.auth.UserNotFoundException;
import sanets.dev.animechallenges.mapper.QuestMapper;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.Quest;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.BadgeRepository;
import sanets.dev.animechallenges.repository.QuestRepository;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.UUID;

import static sanets.dev.animechallenges.repository.specification.QuestSpecification.hasDifficulty;
import static sanets.dev.animechallenges.repository.specification.QuestSpecification.hasMaxAttempts;
import static sanets.dev.animechallenges.repository.specification.QuestSpecification.hasRewardPoints;
import static sanets.dev.animechallenges.repository.specification.QuestSpecification.isActive;
import static sanets.dev.animechallenges.repository.specification.QuestSpecification.titleContains;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestService {

    private static final String BADGE_NOT_FOUND_MSG = "Badge not found in database by id";
    private static final String USER_NOT_FOUND_MSG = "User not found in database by id";
    private static final String QUEST_NOT_FOUND_MSG = "Quest not found in  database by id";
    private static final String INVALID_ACCESS_MSG = "Invalid access!";

    private final QuestRepository questRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final QuestMapper questMapper;

    public void createQuest(CreateQuestRequestDto createQuestRequestDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User creator = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));

        Badge badge = badgeRepository.findBadgeByUid(createQuestRequestDto.getBadge())
                .orElseThrow(() -> new BadgeNotFoundException(BADGE_NOT_FOUND_MSG));


        Quest quest = questMapper.toQuest(createQuestRequestDto);
        quest.setBadge(badge);
        quest.setCreator(creator);

        Quest savedQuest = questRepository.save(quest);
        log.info("Quest created successfully with id: {}", savedQuest.getUid());
    }

    public Page<QuestResponseDto> getQuestsWithFilter(QuestFilterDto filterDto, Pageable pageable) {
        Specification<Quest> spec = Specification.where(titleContains(filterDto.getTitle()))
                .and(isActive(filterDto.getIsActive()))
                .and(hasDifficulty(filterDto.getDifficulty()))
                .and(hasRewardPoints(filterDto.getRewardPoints()))
                .and(hasMaxAttempts(filterDto.getMaxAttempts()));

        Page<Quest> quests = questRepository.findAll(spec, pageable);

        return quests.map(questMapper::toQuestResponseDto);
    }

    public void updateQuest(UUID questUid, UpdateQuestRequestDto questRequestDto) {
        Quest quest = findQuestByUid(questUid);

        if (!hasUserAccess(quest.getCreator())) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }

        questMapper.updateQuestFromDto(questRequestDto, quest);

        Quest savedQuest = questRepository.save(quest);
        log.info("Quest updated successfully with id: {}", savedQuest.getUid());
    }

    public void deleteQuest(UUID questUid) {
        Quest quest = findQuestByUid(questUid);

        if (!hasUserAccess(quest.getCreator())) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }

        quest.setIsActive(false);

        Quest savedQuest = questRepository.save(quest);
        log.info("Quest deleted successfully with id: {}", savedQuest.getUid());
    }

    private Quest findQuestByUid(UUID questUid) {
        return questRepository.findByUid(questUid)
                .orElseThrow( () -> new QuestNotFoundException(QUEST_NOT_FOUND_MSG));
    }

    private boolean hasUserAccess(User creator) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String whoIsTrying = authentication.getName();

        boolean isUserAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("admin"));

        return whoIsTrying.equals(creator.getUsername()) || isUserAdmin;
    }

}
