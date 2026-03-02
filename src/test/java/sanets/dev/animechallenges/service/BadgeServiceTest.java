package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.badge.BadgeRequestDto;
import sanets.dev.animechallenges.exception.badge.BadgeNotFoundException;
import sanets.dev.animechallenges.exception.media.MediaNotUploadedException;
import sanets.dev.animechallenges.mapper.BadgeMapper;
import sanets.dev.animechallenges.model.badge.Badge;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.repository.BadgeRepository;
import sanets.dev.animechallenges.repository.UserBadgeRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;
    @Mock
    private BadgeMapper badgeMapper;
    @Mock
    private MediaService mediaService;
    @Mock
    private QuestParticipationService questParticipationService;
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
    void deleteBadge_shouldRecallSaveAndSetIsActiveFalse_whenc(){
        Badge badge = new Badge();
        badge.setActive(true);

        when(badgeRepository.findBadgeByUid(any())).thenReturn(Optional.of(badge));

        badgeService.archiveBadge(badge.getUid());

        assertFalse(badge.isActive());
        verify(badgeRepository).save(badge);
    }

    @Test
    void deleteBadge_shouldThrowBadgeNotFoundException_whenBadgeNotFound() {
        UUID fakeUid = UUID.randomUUID();
        when(badgeRepository.findBadgeByUid(any())).thenReturn(Optional.empty());

        assertThrows(BadgeNotFoundException.class, () -> badgeService.archiveBadge(fakeUid));
    }
}