package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sanets.dev.animechallenges.dto.participation.CreateParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.UpdateQuestParticipationRequestDto;
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
import sanets.dev.animechallenges.model.user.UserRole;
import sanets.dev.animechallenges.repository.QuestParticipationRepository;
import sanets.dev.animechallenges.security.SecurityUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestParticipationServiceTest {

    @Mock
    private QuestParticipationRepository questParticipationRepository;
    @Mock
    private UserService userService;
    @Mock
    private QuestService questService;
    @Mock
    private QuestParticipationMapper questParticipationMapper;

    @InjectMocks
    private QuestParticipationService questParticipationService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    private User user;
    private User creator;
    private Quest quest;
    private QuestParticipation participation;

    @BeforeEach
    void setUp() {
        creator = User.builder()
                .uid(UUID.randomUUID())
                .username("creator")
                .role(UserRole.ROLE_USER)
                .build();

        user = User.builder()
                .uid(UUID.randomUUID())
                .username("user")
                .role(UserRole.ROLE_USER)
                .build();

        quest = Quest.builder()
                .uid(UUID.randomUUID())
                .creator(creator)
                .isActive(true)
                .build();

        participation = QuestParticipation.builder()
                .uid(UUID.randomUUID())
                .performer(user)
                .quest(quest)
                .questStatus(QuestStatus.PENDING)
                .build();

        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    void createQuestParticipation_success() {
        securityUtilsMock.when(SecurityUtils::getCurrentUserUid).thenReturn(user.getUid());
        CreateParticipationRequestDto dto = new CreateParticipationRequestDto();
        dto.setQuestUid(quest.getUid());

        QuestParticipationResponseDto expectedResponse = new QuestParticipationResponseDto();
        expectedResponse.setQuestStatus(QuestStatus.PENDING);

        when(questService.getQuestByUid(quest.getUid())).thenReturn(quest);
        when(userService.getUserByUid(user.getUid())).thenReturn(user);
        when(questParticipationRepository.existsByPerformerAndQuest(user, quest)).thenReturn(false);
        when(questParticipationRepository.save(any(QuestParticipation.class))).thenReturn(participation);
        when(questParticipationMapper.toQuestParticipation(any(User.class), any(Quest.class), any()))
                .thenReturn(participation);
        when(questParticipationMapper.toQuestParticipationResponseDto(any(QuestParticipation.class)))
                .thenReturn(expectedResponse);

        QuestParticipationResponseDto result = questParticipationService.createQuestParticipation(dto);

        assertNotNull(result);
        assertEquals(QuestStatus.PENDING, result.getQuestStatus());
        verify(questParticipationRepository).save(any(QuestParticipation.class));
    }

    @Test
    void createQuestParticipation_throwsAlreadyParticipating() {
        securityUtilsMock.when(SecurityUtils::getCurrentUserUid).thenReturn(user.getUid());
        CreateParticipationRequestDto dto = new CreateParticipationRequestDto();
        dto.setQuestUid(quest.getUid());

        when(questService.getQuestByUid(quest.getUid())).thenReturn(quest);
        when(userService.getUserByUid(user.getUid())).thenReturn(user);
        when(questParticipationRepository.existsByPerformerAndQuest(user, quest)).thenReturn(true);

        assertThrows(AlreadyParticipatingException.class, () -> questParticipationService.createQuestParticipation(dto));
        verify(questParticipationRepository, never()).save(any());
    }

    @Test
    void createQuestParticipation_throwsQuestNotAvailable() {
        securityUtilsMock.when(SecurityUtils::getCurrentUserUid).thenReturn(user.getUid());
        quest.setIsActive(false);
        CreateParticipationRequestDto dto = new CreateParticipationRequestDto();
        dto.setQuestUid(quest.getUid());

        when(questService.getQuestByUid(quest.getUid())).thenReturn(quest);
        when(userService.getUserByUid(user.getUid())).thenReturn(user);
        when(questParticipationRepository.existsByPerformerAndQuest(user, quest)).thenReturn(false);

        assertThrows(QuestNotAvailable.class, () -> questParticipationService.createQuestParticipation(dto));
        verify(questParticipationRepository, never()).save(any());
    }

    @Test
    void createQuestParticipation_throwsInvalidAccess_whenCreator() {
        securityUtilsMock.when(SecurityUtils::getCurrentUserUid).thenReturn(creator.getUid());
        CreateParticipationRequestDto dto = new CreateParticipationRequestDto();
        dto.setQuestUid(quest.getUid());

        when(questService.getQuestByUid(quest.getUid())).thenReturn(quest);
        when(userService.getUserByUid(creator.getUid())).thenReturn(creator);
        when(questParticipationRepository.existsByPerformerAndQuest(creator, quest)).thenReturn(false);

        assertThrows(InvalidAccessException.class, () -> questParticipationService.createQuestParticipation(dto));
        verify(questParticipationRepository, never()).save(any());
    }

    @Test
    void getAllQuestParticipationByUserUid_success() {
        UUID userUid = user.getUid();
        when(questParticipationRepository.findAllByPerformerUid(userUid)).thenReturn(List.of(participation));
        when(questParticipationMapper.toQuestParticipationResponseDto(any(QuestParticipation.class))).thenReturn(new QuestParticipationResponseDto());

        List<QuestParticipationResponseDto> result = questParticipationService.getAllQuestParticipationByUserUid(userUid);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateQuestParticipation_success() {
        UpdateQuestParticipationRequestDto dto = new UpdateQuestParticipationRequestDto();
        dto.setParticipationUid(participation.getUid());

        securityUtilsMock.when(() -> SecurityUtils.validateUserAccessByUsername(user.getUsername()))
                .thenAnswer(invocation -> null);

        QuestParticipationResponseDto responseDto = new QuestParticipationResponseDto();

        when(questParticipationRepository.findQuestParticipationByUid(participation.getUid()))
                .thenReturn(Optional.of(participation));
        when(questParticipationRepository.save(participation)).thenReturn(participation);
        when(questParticipationMapper.toQuestParticipationResponseDto(participation)).thenReturn(responseDto);

        QuestParticipationResponseDto result = questParticipationService.updateQuestParticipation(dto);

        assertNotNull(result);
        verify(questParticipationMapper).updateQuestParticipationFromDto(dto, participation);
        verify(questParticipationRepository).save(participation);
    }

    @Test
    void updateQuestParticipation_throwsNotFound() {
        UpdateQuestParticipationRequestDto dto = new UpdateQuestParticipationRequestDto();
        dto.setParticipationUid(UUID.randomUUID());

        when(questParticipationRepository.findQuestParticipationByUid(any())).thenReturn(Optional.empty());

        assertThrows(ParticipationNotFoundException.class, () -> questParticipationService.updateQuestParticipation(dto));
    }

    @Test
    void updateQuestParticipation_throwsInvalidAccess() {
        UpdateQuestParticipationRequestDto dto = new UpdateQuestParticipationRequestDto();
        dto.setParticipationUid(participation.getUid());

        when(questParticipationRepository.findQuestParticipationByUid(participation.getUid()))
                .thenReturn(Optional.of(participation));

        securityUtilsMock.when(() -> SecurityUtils.validateUserAccessByUsername(user.getUsername()))
                .thenThrow(new InvalidAccessException("Access Denied"));

        assertThrows(InvalidAccessException.class, () -> questParticipationService.updateQuestParticipation(dto));

        verify(questParticipationRepository, never()).save(any());
    }

    @Test
    void deleteParticipationByUid_asUser_setsCancelled() {
        UUID partUid = participation.getUid();

        participation.getPerformer().setRole(UserRole.ROLE_USER);

        when(questParticipationRepository.findQuestParticipationByUid(partUid))
                .thenReturn(Optional.of(participation));

        securityUtilsMock.when(() -> SecurityUtils.validateUserAccessByUsername(user.getUsername()))
                .thenAnswer(invocation -> null);

        questParticipationService.deleteParticipationByUid(partUid);

        assertEquals(QuestStatus.CANCELLED, participation.getQuestStatus());
        verify(questParticipationRepository).save(participation);
    }

    @Test
    void deleteParticipationByUid_whenIsAdmin_setsRejected() {
        UUID partUid = participation.getUid();

        participation.getPerformer().setRole(UserRole.ROLE_ADMIN);

        when(questParticipationRepository.findQuestParticipationByUid(partUid))
                .thenReturn(Optional.of(participation));

        securityUtilsMock.when(() -> SecurityUtils.validateUserAccessByUsername(any()))
                .thenAnswer(invocation -> null);

        securityUtilsMock.when(SecurityUtils::isAdmin)
                        .thenReturn(true);

        securityUtilsMock.when(SecurityUtils::getCurrentUsername)
                .thenReturn("someUsernameDifferentByPerformer");

        questParticipationService.deleteParticipationByUid(partUid);
        assertEquals(QuestStatus.REJECTED, participation.getQuestStatus());
        verify(questParticipationRepository).save(participation);
    }
}