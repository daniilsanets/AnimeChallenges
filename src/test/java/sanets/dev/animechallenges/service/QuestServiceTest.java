package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sanets.dev.animechallenges.dto.quest.CreateQuestRequestDto;
import sanets.dev.animechallenges.mapper.QuestMapper;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.Quest;
import sanets.dev.animechallenges.model.QuestsDifficulty;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.QuestRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestServiceTest {

    @Mock
    private QuestRepository questRepository;
    @Mock
    private BadgeService badgeService;
    @Mock
    private UserService userService;
    @Mock
    private QuestMapper questMapper;

    @InjectMocks
    private QuestService questService;

    private User creator;
    private Badge badge;
    private Quest quest;

    @BeforeEach
    void setUp() {

        creator = User.builder()
                .uid(UUID.randomUUID())
                .username("danechka")
                .build();

        badge = Badge.builder()
                .uid(UUID.randomUUID())
                .title("Wall hero")
                .build();

        quest = Quest.builder()
                .uid(UUID.randomUUID())
                .creator(creator)
                .description("desc")
                .title("title")
                .maxAttempts(1)
                .difficulty(QuestsDifficulty.MEDIUM)
                .rewardPoints(10)
                .badge(badge)
                .isActive(true)
                .build();
    }

    @Test
    void createQuest_ShouldSaveAndLogCorrectly() {

        CreateQuestRequestDto dto = new CreateQuestRequestDto();
        dto.setBadge(badge.getUid());
        UUID creatorUid = creator.getUid();

        lenient().doNothing().when(userService).validateUserAccess(creatorUid);

        when(userService.getUserByUidOrThrow(any())).thenReturn(creator);
        when(badgeService.getBadgeByUidOrThrow(any())).thenReturn(badge);

        when(questMapper.toQuest(dto)).thenReturn(quest);
        when(questRepository.save(quest)).thenReturn(quest);

        questService.createQuest(dto);

        verify(questRepository).save(quest);
    }

    @Test
    void deleteQuest_ShouldSoftDelete_AndSaveResult() {

        when(questRepository.findByUid(quest.getUid()))
                .thenReturn(Optional.of(quest));

        doNothing().when(userService).validateUserAccess(creator.getUid());

        when(questRepository.save(quest)).thenReturn(quest);

        questService.deleteQuest(quest.getUid());

        assertFalse(quest.getIsActive());
        verify(questRepository).save(quest);
    }
}
