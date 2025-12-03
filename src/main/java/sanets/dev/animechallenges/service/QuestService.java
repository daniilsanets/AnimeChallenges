package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanets.dev.animechallenges.dto.quest.QuestFilterDto;
import sanets.dev.animechallenges.dto.quest.QuestRequestDto;
import sanets.dev.animechallenges.dto.quest.QuestRequestToUpdateDto;
import sanets.dev.animechallenges.exception.badgeExceptions.BadgeNotFoundException;
import sanets.dev.animechallenges.exception.commonExceptions.InvalidAccessException;
import sanets.dev.animechallenges.exception.questExceptions.QuestNotCreatedException;
import sanets.dev.animechallenges.exception.questExceptions.QuestNotDeletedException;
import sanets.dev.animechallenges.exception.questExceptions.QuestNotFoundException;
import sanets.dev.animechallenges.exception.questExceptions.QuestNotUpdatedException;
import sanets.dev.animechallenges.exception.authExceptions.UserNotFoundException;
import sanets.dev.animechallenges.mapper.QuestMapper;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.Quest;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.BadgeRepository;
import sanets.dev.animechallenges.repository.QuestRepository;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestService {

    private static final String BADGE_NOT_FOUND_MSG = "Badge not found";
    private static final String USER_NOT_FOUND_MSG = "User not found!";
    private static final String QUEST_NOT_FOUND_MSG = "Quest not found!";
    private static final String QUEST_NOT_DELETED_MSG = "Quest not deleted!";
    private static final String INVALID_ACCESS_MSG = "Invalid access!";
    private static final String QUEST_NOT_UPDATED_MSG = "Quest not updated!";

    private final QuestRepository questRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final QuestMapper questMapper;

    public void createQuest(QuestRequestDto questRequestDto)
            throws BadgeNotFoundException, UserNotFoundException, QuestNotFoundException, QuestNotCreatedException
    {
        User creator = userRepository.findById(questRequestDto.getCreator())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));

        Badge badge = badgeRepository.findBadgeByUid(questRequestDto.getBadge())
                .orElseThrow(() -> new BadgeNotFoundException(BADGE_NOT_FOUND_MSG));


        Quest quest = questMapper.toQuest(questRequestDto);
        quest.setBadge(badge);
        quest.setCreator(creator);

        try {
            questRepository.save(quest);
        } catch (DataAccessException ex) {
            log.error("Something went wrong with saving quests", ex);
            throw new QuestNotCreatedException(QUEST_NOT_FOUND_MSG);
        }
    }

    public Page<Quest> getQuestsWithFilter(QuestFilterDto filterDto, Pageable pageable){
        Specification<Quest> spec = (root, query, cb) -> cb.conjunction();

        if(filterDto.getTitle() != null && !filterDto.getTitle().isEmpty()){
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + filterDto.getTitle() + "%")
            );
        }

        if(filterDto.getIsActive() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActive"), filterDto.getIsActive())
            );
        }

        if(filterDto.getDifficulty() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("difficulty"), filterDto.getDifficulty())
            );
        }

        if(filterDto.getRewardPoints() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("rewardPoints"), filterDto.getRewardPoints())
                    );
        }

        if(filterDto.getMaxAttempts() != null) {
            spec =spec.and((root, query, cb) ->
                    cb.equal(root.get("maxAttempts"), filterDto.getMaxAttempts())
            );
        }

        return questRepository.findAll(spec, pageable);
    }

    @Transactional
    public void updateQuest(UUID questUid, QuestRequestToUpdateDto questRequestDto)
            throws QuestNotFoundException
    {
        Quest quest = findQuest(questUid);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));

        if (!quest.getCreator().getUid().equals(currentUser.getUid())) {
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }

        questMapper.updateQuestFromDto(questRequestDto, quest);

        if (questRequestDto.getBadge() != null) {
            if (!quest.getBadge().getUid().equals(questRequestDto.getBadge())) {
                Badge newBadge = badgeRepository.findBadgeByUid(questRequestDto.getBadge())
                        .orElseThrow(() -> new BadgeNotFoundException(BADGE_NOT_FOUND_MSG));
                quest.setBadge(newBadge);
            }
        }

        try {
            questRepository.save(quest);
        } catch (DataAccessException ex) {
            log.error("Error updating quest {}", questUid, ex);
            throw new QuestNotUpdatedException(QUEST_NOT_UPDATED_MSG);
        }

    }

    //I think we need to add check who's gonna delete Quest(It must do only creators or admins)
    public void deleteQuest(UUID questUid) throws QuestNotFoundException, QuestNotDeletedException {
        Quest quest = questRepository.findByUid(questUid)
                .orElseThrow(() -> new QuestNotFoundException(QUEST_NOT_DELETED_MSG));

        quest.setIsActive(false);

        try {
            questRepository.save(quest);
        } catch (DataAccessException ex) {
            log.error(ex.getMessage(), ex);
            throw new QuestNotDeletedException(QUEST_NOT_DELETED_MSG);
        }
    }

    private Quest findQuest(UUID questUid) throws QuestNotFoundException {
        return questRepository.findByUid(questUid)
                .orElseThrow( () -> new QuestNotFoundException(QUEST_NOT_FOUND_MSG));
    }

}
