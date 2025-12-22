package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.participation.UpdateQuestParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.CreateParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.QuestParticipationResponseDto;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.exception.participation.AlreadyParticipatingException;
import sanets.dev.animechallenges.exception.participation.ParticipationNotFoundException;
import sanets.dev.animechallenges.exception.quest.QuestNotAvailable;
import sanets.dev.animechallenges.mapper.QuestParticipationMapper;
import sanets.dev.animechallenges.model.Quest;
import sanets.dev.animechallenges.model.QuestParticipation;
import sanets.dev.animechallenges.model.QuestStatus;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.model.UserRole;
import sanets.dev.animechallenges.repository.QuestParticipationRepository;
import sanets.dev.animechallenges.security.SecurityUtils;

import java.util.List;
import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.ALREADY_PARTICIPATING_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.INVALID_ACCESS_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.QUEST_NOT_AVAILABLE_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.QUEST_PARTICIPATION_NOT_FOUND_MSG;

@RequiredArgsConstructor
@Service
public class QuestParticipationService {

    private final QuestParticipationRepository questParticipationRepository;
    private final UserService userService;
    private final QuestService questService;
    private final QuestParticipationMapper questParticipationMapper;

    /// Do I need to add mapper to this(because I take only one field from dto ) i mean from CreateParticipationRequestDto
    public QuestParticipationResponseDto createQuestParticipation(CreateParticipationRequestDto dto) {
        Quest questToParticipate = questService.getQuestByUid(dto.getQuestUid());

        User currentUser = userService.getUserByUid(SecurityUtils.getCurrentUserUid());

        validateCanParticipate(currentUser, questToParticipate);

        QuestParticipation participation = QuestParticipation.builder()
                .performer(currentUser)
                .quest(questToParticipate)
                .questStatus(QuestStatus.PENDING)
                .build();

        questParticipationRepository.save(participation);

        return questParticipationMapper.toQuestParticipationResponseDto(participation);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<QuestParticipation> getAllQuestParticipationByUserUid(UUID userUid) {
       return questParticipationRepository.findAllByPerformerUid(userUid);
    }

    public QuestParticipationResponseDto updateQuestParticipation(UpdateQuestParticipationRequestDto dto){
        QuestParticipation questParticipationToUpdate = getQuestParticipationByUid(dto.getParticipationUid());

        User performer = questParticipationToUpdate.getPerformer();

        SecurityUtils.validateUserAccessByUsername(performer.getUsername());

        questParticipationMapper.updateQuestParticipationFromDto(dto, questParticipationToUpdate);

        questParticipationRepository.save(questParticipationToUpdate);

        return questParticipationMapper.toQuestParticipationResponseDto(questParticipationToUpdate);
    }

    public void deleteParticipationByUid(UUID questParticipation) {
        QuestParticipation participationToDelete = getQuestParticipationByUid(questParticipation);

        User performer = participationToDelete.getPerformer();

        SecurityUtils.validateUserAccessByUsername(performer.getUsername());

        if (performer.getRole().equals(UserRole.ROLE_ADMIN)){
            participationToDelete.setQuestStatus(QuestStatus.REJECTED);
        } else {
            participationToDelete.setQuestStatus(QuestStatus.CANCELLED);
        }

        questParticipationRepository.save(participationToDelete);
    }

    private QuestParticipation getQuestParticipationByUid(UUID questParticipation) {
        return questParticipationRepository.findQuestParticipationByUid(questParticipation)
                .orElseThrow(() -> new ParticipationNotFoundException(QUEST_PARTICIPATION_NOT_FOUND_MSG));
    }

    /// Maybe we need a limit for quantity users who can participate (Participation limit)
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
