package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.BadgeRequestDto;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;
    @Mock
    private BadgeMapper badgeMapper;
    @Mock
    private MediaService mediaService;
    @Mock
    private QuestParticipationRepository questParticipationRepository;
    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private BadgeService badgeService;

    @Test
    void createBadge_shouldSaveBadge_whenSuccessful() {
        MultipartFile file = mock(MultipartFile.class);
        BadgeRequestDto dto = new BadgeRequestDto();
        dto.setTitle("Test Badge");

        Media media = Media.builder().storageKey("test-key.png").build();
        Badge badge = Badge.builder().title("Test Badge").build();

        when(mediaService.upload(file)).thenReturn(media);
        when(badgeMapper.toBadge(eq(dto), eq(media), anyString())).thenReturn(badge);

        badgeService.createBadge(dto, file);

        verify(badgeRepository).save(badge);
        verify(mediaService, never()).deleteFileOnly(any());
    }

    @Test
    void createBadge_shouldDeleteFile_whenBadgeSaveFails() {
        MultipartFile file = mock(MultipartFile.class);
        BadgeRequestDto dto = new BadgeRequestDto();
        String storageKey = "rollback-me.png";

        Media media = Media.builder().storageKey(storageKey).build();
        Badge badge = Badge.builder().build();

        when(mediaService.upload(file)).thenReturn(media);
        when(badgeMapper.toBadge(eq(dto), eq(media), anyString())).thenReturn(badge);
        when(badgeRepository.save(badge)).thenThrow(new DataIntegrityViolationException("Duplicate code"));

        assertThrows(MediaNotUploadedException.class, () -> badgeService.createBadge(dto, file));

        verify(mediaService).deleteFileOnly(storageKey);
    }

    @Test
    void deleteBadge_shouldRecallSaveAndSetIsActiveFalse_whenSuccess(){
        Badge badge = new Badge();
        badge.setActive(true);

        when(badgeRepository.findBadgeByUid(any())).thenReturn(Optional.of(badge));

        badgeService.deleteBadge(badge.getUid());

        assertFalse(badge.isActive());
        verify(badgeRepository).save(badge);
    }

    @Test
    void deleteBadge_shouldThrowBadgeNotFoundException_whenBadgeNotFound() {
        UUID fakeUid = UUID.randomUUID();
        when(badgeRepository.findBadgeByUid(any())).thenReturn(Optional.empty());

        assertThrows(BadgeNotFoundException.class, () -> badgeService.deleteBadge(fakeUid));
    }

    @Test
    void processQuestCompletion_shouldAssignAchievement_whenConditionMet() {
        UUID userId = UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();

        User user = User.builder()
                .uid(userId)
                .username("user")
                .build();

        Badge badge = Badge.builder()
                .title("Test Badge")
                .uid(badgeId)
                .build();

        Map<String, Object> rule = Map.of("count", 5);
        badge.setRule(rule);

        Quest quest = new Quest();

        when(questParticipationRepository.countByPerformerAndQuestStatus(user, QuestStatus.APPROVED)).thenReturn(5L);

        when(userBadgeRepository.findBadgeIdsByUser(userId)).thenReturn(new HashSet<>());
        when(badgeRepository.findByBadgeType(BadgeType.ACHIEVEMENT)).thenReturn(List.of(badge));

        badgeService.processQuestCompletion(user, quest);

        verify(userBadgeRepository).insertUserBadge(eq(userId), eq(badgeId), any());
    }

    @Test
    void processQuestCompletion_shouldAssignQuestReward_whenQuestHasBadge(){
        UUID userUid =  UUID.randomUUID();
        UUID badgeId = UUID.randomUUID();

        User user = User.builder()
                .uid(userUid)
                .build();

        Badge badge = Badge.builder()
                .uid(badgeId)
                .build();
        Quest quest = Quest.builder()
                .badge(badge)
                .build();

        when(questParticipationRepository.countByPerformerAndQuestStatus(any(), any()))
                .thenReturn(0L);

        when(userBadgeRepository.findBadgeIdsByUser(userUid))
                .thenReturn(new HashSet<>());

        when(badgeRepository.findByBadgeType(BadgeType.ACHIEVEMENT))
                .thenReturn(List.of());
        badgeService.processQuestCompletion(user, quest);

        verify(userBadgeRepository).insertUserBadge(eq(userUid), eq(badgeId), any());
    }
}