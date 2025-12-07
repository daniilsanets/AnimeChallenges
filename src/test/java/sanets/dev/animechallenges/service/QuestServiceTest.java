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

import java.util.Collections;
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
    void createQuest_ShouldSaveAndLogCorrectly() {
        CreateQuestRequestDto dto = new CreateQuestRequestDto();
        dto.setBadge(badge.getUid());

        when(authentication.getName()).thenReturn(creator.getUsername());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(creator.getUsername())).thenReturn(Optional.of(creator));
        when(badgeRepository.findBadgeByUid(dto.getBadge())).thenReturn(Optional.of(badge));

        Quest rawQuest = quest;
        when(questMapper.toQuest(dto)).thenReturn(rawQuest);

        Quest savedQuest = Quest.builder()
                .uid(UUID.randomUUID())
                .title("Saved Title")
                .creator(creator)
                .build();

        when(questRepository.save(rawQuest)).thenReturn(savedQuest);

        questService.createQuest(dto);

        verify(questRepository).save(rawQuest);
    }

    @Test
    void deleteQuest_ShouldSoftDelete_AndSaveResult() {
        UUID questId = quest.getUid();
        quest.setIsActive(true);

        when(questRepository.findByUid(questId)).thenReturn(Optional.of(quest));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(creator.getUsername());
        SecurityContextHolder.setContext(securityContext);

        when(questRepository.save(quest)).thenReturn(quest);

        questService.deleteQuest(questId);

        assertFalse(quest.getIsActive());
        verify(questRepository).save(quest);
    }

    @Test
    void test_updateQuest_ShouldThrowInvalidAccessException_whenUserIsNotCreator() {
        User notCreator = User.builder()
                .uid(UUID.randomUUID())
                .username("Almost danechka")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(notCreator.getUsername());
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        Quest existingQuest = Quest.builder()
                .creator(creator)
                .build();
        when(questRepository.findByUid(any())).thenReturn(Optional.of(existingQuest));
        SecurityContextHolder.setContext(securityContext);

        assertThrows(InvalidAccessException.class,
                () -> questService.updateQuest(UUID.randomUUID(), new UpdateQuestRequestDto())
        );
    }

}
