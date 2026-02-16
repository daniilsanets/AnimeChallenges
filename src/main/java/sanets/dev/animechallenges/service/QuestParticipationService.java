package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.participation.UpdateQuestParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.CreateParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.QuestParticipationResponseDto;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.exception.participation.AlreadyParticipatingException;
import sanets.dev.animechallenges.exception.participation.ParticipationNotFoundException;
import sanets.dev.animechallenges.exception.quest.QuestNotAvailable;
import sanets.dev.animechallenges.mapper.QuestParticipationMapper;
import sanets.dev.animechallenges.model.quest.Quest;
import sanets.dev.animechallenges.model.quest.QuestParticipation;
import sanets.dev.animechallenges.model.quest.QuestStatus;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.repository.QuestParticipationRepository;
import sanets.dev.animechallenges.security.SecurityUtils;

import java.util.UUID;
import static sanets.dev.animechallenges.exception.ErrorMessages.ALREADY_PARTICIPATING_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.INVALID_ACCESS_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.QUEST_NOT_AVAILABLE_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.QUEST_PARTICIPATION_NOT_FOUND_MSG;
import static sanets.dev.animechallenges.security.SecurityUtils.getCurrentUsername;
import static sanets.dev.animechallenges.security.SecurityUtils.isAdmin;
import static sanets.dev.animechallenges.security.SecurityUtils.validateUserAccessByUserUid;
import static sanets.dev.animechallenges.security.SecurityUtils.validateUserAccessByUsername;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestParticipationService {

    private final QuestParticipationRepository questParticipationRepository;
    private final UserService userService;
    private final QuestService questService;
    private final QuestParticipationMapper questParticipationMapper;

    public QuestParticipationResponseDto createQuestParticipation(CreateParticipationRequestDto dto) {
        Quest questToParticipate = questService.getQuestByUid(dto.getQuestUid());

        User currentUser = userService.getUserByUid(SecurityUtils.getCurrentUserUid());

        validateCanParticipate(currentUser, questToParticipate);

        QuestParticipation participation = questParticipationMapper.toQuestParticipation(currentUser, questToParticipate, dto.getScore());
        participation.setQuestStatus(QuestStatus.PENDING);

        questParticipationRepository.save(participation);

        return questParticipationMapper.toQuestParticipationResponseDto(participation);
    }

    public Page<QuestParticipationResponseDto> getAllQuestParticipationByUserUid(UUID userUid, Pageable pageable) {
        validateUserAccessByUserUid(userUid);

        return questParticipationRepository
                .findAllByPerformerUid(userUid, pageable)
                .map(questParticipationMapper::toQuestParticipationResponseDto);
    }

    //todo: split updating status, cannot be after pending(state pattern)
    public QuestParticipationResponseDto updateQuestParticipation(UUID participationUid, UpdateQuestParticipationRequestDto dto){
        QuestParticipation questParticipationToUpdate = getQuestParticipationByUid(participationUid);

        User performer = questParticipationToUpdate.getPerformer();

        validateUserAccessByUsername(performer.getUsername());

        questParticipationMapper.updateQuestParticipationFromDto(dto, questParticipationToUpdate);

        questParticipationRepository.save(questParticipationToUpdate);

        return questParticipationMapper.toQuestParticipationResponseDto(questParticipationToUpdate);
    }


    public void cancelParticipationByUid(UUID questParticipation) {
        QuestParticipation participationToDelete = getQuestParticipationByUid(questParticipation);

        User performer = participationToDelete.getPerformer();

        log.debug("Validate user access in delete Participation function ");
        validateUserAccessByUsername(performer.getUsername());

        if (isAdmin() && !(getCurrentUsername().equals(performer.getUsername())) ) {
            participationToDelete.setQuestStatus(QuestStatus.REJECTED);
        } else {
            participationToDelete.setQuestStatus(QuestStatus.CANCELLED);
        }

        questParticipationRepository.save(participationToDelete);
    }

    public QuestParticipation getQuestParticipationByUid(UUID questParticipation) {
        return questParticipationRepository.findQuestParticipationByUid(questParticipation)
                .orElseThrow(() -> new ParticipationNotFoundException(QUEST_PARTICIPATION_NOT_FOUND_MSG));
    }

    public QuestParticipationResponseDto getQuestParticipationResponseByUid(UUID questParticipation) {
        return questParticipationMapper.toQuestParticipationResponseDto(getQuestParticipationByUid(questParticipation));
    }

    public Long getCountByPerformerAndQuestStatus(UUID performerUid, QuestStatus questStatus) {
        User performer = userService.getUserByUid(performerUid);
        return questParticipationRepository.countByPerformerAndQuestStatus(performer, questStatus);
    }

    private void validateCanParticipate(User user, Quest quest){
        if (questParticipationRepository.existsByPerformerAndQuest(user, quest)){
            throw new AlreadyParticipatingException(ALREADY_PARTICIPATING_MSG);
        }

        if (quest.getIsActive() == false){
            throw new QuestNotAvailable(QUEST_NOT_AVAILABLE_MSG);
        }

        if (quest.getCreator().getUid().equals(user.getUid())){
            throw new InvalidAccessException(INVALID_ACCESS_MSG);
        }
    }

}
