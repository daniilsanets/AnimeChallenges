package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sanets.dev.animechallenges.dto.quest.CreateQuestRequestDto;
import sanets.dev.animechallenges.mapper.QuestMapper;
import sanets.dev.animechallenges.model.badge.Badge;
import sanets.dev.animechallenges.model.quest.Quest;
import sanets.dev.animechallenges.model.quest.QuestsDifficulty;
import sanets.dev.animechallenges.model.user.User;
import sanets.dev.animechallenges.repository.QuestRepository;
import sanets.dev.animechallenges.security.SecurityUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

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

    private void mockSecurityContext(String username) {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn(username);
    }

    @Test
    void createQuest_ShouldSaveAndLogCorrectly() {
        CreateQuestRequestDto dto = new CreateQuestRequestDto();
        dto.setBadge(badge.getUid());

        try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {

            securityUtilsMock.when(SecurityUtils::getCurrentUserUid).thenReturn(creator.getUid());

            lenient().when(userService.getUserByUid(creator.getUid())).thenReturn(creator);
            when(badgeService.getBadgeByUid(any())).thenReturn(badge);
            when(questMapper.toQuest(dto)).thenReturn(quest);
            when(questRepository.save(quest)).thenReturn(quest);

            questService.createQuest(dto);

            verify(questRepository).save(quest);
        }
    }

    @Test
    void deleteQuest_ShouldSoftDelete_AndSaveResult() {

        when(questRepository.findByUid(quest.getUid()))
                .thenReturn(Optional.of(quest));

        MockedStatic<SecurityUtils> mockedCall = mockStatic(SecurityUtils.class, invoca -> {
            return null;
        });

        when(questRepository.save(quest)).thenReturn(quest);
        mockSecurityContext("danechka");

        questService.deleteQuest(quest.getUid());

        mockedCall.close();
        assertFalse(quest.getIsActive());
        verify(questRepository).save(quest);
    }
}
