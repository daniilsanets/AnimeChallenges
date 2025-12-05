package sanets.dev.animechallenges.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sanets.dev.animechallenges.dto.quest.CreateQuestRequestDto;
import sanets.dev.animechallenges.dto.quest.UpdateQuestRequestDto;
import sanets.dev.animechallenges.exception.common.InvalidAccessException;
import sanets.dev.animechallenges.mapper.QuestMapper;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.Quest;
import sanets.dev.animechallenges.model.QuestsDifficulty;
import sanets.dev.animechallenges.model.User;
import sanets.dev.animechallenges.repository.BadgeRepository;
import sanets.dev.animechallenges.repository.QuestRepository;
import sanets.dev.animechallenges.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestServiceTest {
    @Mock
    private QuestRepository questRepository;
    @Mock
    private BadgeRepository badgeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestMapper questMapper;

    @InjectMocks
    private QuestService questService;

    private User creator;
    private Badge badge;
    private Quest quest;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp(){
        securityContext = Mockito.mock(SecurityContext.class);
        authentication = Mockito.mock(Authentication.class);

        creator = User.builder()
                .uid(UUID.randomUUID())
                .username("danechka")
                .build();

        badge = Badge.builder()
                .uid(UUID.randomUUID())
                .title("Wall hero")
                .build();

        quest = Quest.builder()
                .creator(creator)
                .description("description")
                .maxAttempts(1)
                .title("title")
                .rewardPoints(7)
                .difficulty(QuestsDifficulty.MEDIUM)
                .badge(badge)
                .build();
    }

    @Test
    void createQuestShouldCreateQuest_whenSuccessfully() {
        CreateQuestRequestDto createQuestRequestDto = new CreateQuestRequestDto();
        createQuestRequestDto.setTitle("title");
        createQuestRequestDto.setDescription("description");
        createQuestRequestDto.setMaxAttempts(1);
        createQuestRequestDto.setBadge(badge.getUid());
        createQuestRequestDto.setRewardPoints(7);
        createQuestRequestDto.setDifficulty(QuestsDifficulty.MEDIUM);

        when(authentication.getName()).thenReturn(creator.getUsername());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(badgeRepository.findBadgeByUid(createQuestRequestDto.getBadge())).thenReturn(Optional.of(badge));
        when(userRepository.findByUsername(creator.getUsername())).thenReturn(Optional.of(creator));
        when(questMapper.toQuest(createQuestRequestDto)).thenReturn(
                quest
        );

        SecurityContextHolder.setContext(securityContext);

        questService.createQuest(createQuestRequestDto);

        verify(questRepository).save(any());
    }

    @Test
    void test_updateQuest_ShouldThrowInvalidAccessException_whenUserIsNotCreator() {
        User notCreator = User.builder()
                .uid(UUID.randomUUID())
                .username("Almost danechka")
                .build();

        when(authentication.getName()).thenReturn(notCreator.getUsername());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(notCreator));
        when(questRepository.findByUid(any())).thenReturn(Optional.of(Quest.builder()
                        .creator(creator)
                .build()));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(InvalidAccessException.class, () -> questService.updateQuest(quest.getUid(), new UpdateQuestRequestDto()));
    }

    @Test
    void deleteQuestShouldSetQuestsActiveFalse_whenSuccessfullyDeleted() {
        System.out.println(quest.getIsActive());
        when(questRepository.findByUid(any())).thenReturn(Optional.of(quest));

        questService.deleteQuest(quest.getUid());

        assertFalse(quest.getIsActive());
        verify(questRepository).save(quest);
    }
}
