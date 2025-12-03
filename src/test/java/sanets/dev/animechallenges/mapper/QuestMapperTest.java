package sanets.dev.animechallenges.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import sanets.dev.animechallenges.dto.quest.QuestRequestDto;
import sanets.dev.animechallenges.dto.quest.QuestRequestToUpdateDto;
import sanets.dev.animechallenges.model.Badge;
import sanets.dev.animechallenges.model.Quest;
import sanets.dev.animechallenges.model.QuestsDifficulty;
import sanets.dev.animechallenges.model.User;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class QuestMapperTest {

    private final QuestMapper questMapper = Mappers.getMapper(QuestMapper.class);
    private User creator;
    private Badge badge;
    private Quest quest;

    @BeforeEach
    void setup() {
        UUID creatorUid = UUID.randomUUID();
        UUID badgeUid = UUID.randomUUID();

        creator = User.builder()
                .uid(creatorUid)
                .username("creator")
                .build();

        badge = sanets.dev.animechallenges.model.Badge.builder()
                .uid(badgeUid)
                .title("badge")
                .build();

        UUID questId = UUID.randomUUID();
        UUID badgeAfter = UUID.randomUUID();

        quest = Quest.builder()
                .uid(questId)
                .title("test-title")
                .description("before-test-description")
                .badge(Badge.builder().uid(badgeAfter).build())
                .creator(creator)
                .rewardPoints(4)
                .maxAttempts(2)
                .difficulty(QuestsDifficulty.MEDIUM)
                .build();
    }

    @Test
    void questRequestMapToQuest() {

        QuestRequestDto questRequestDto = new QuestRequestDto();
        questRequestDto.setDescription("test-description");
        questRequestDto.setTitle("test-title");
        questRequestDto.setBadge(badge.getUid());
        questRequestDto.setCreator(creator.getUid());
        questRequestDto.setDifficulty(QuestsDifficulty.HARD);
        questRequestDto.setMaxAttempts(3);
        questRequestDto.setRewardPoints(7);

        Quest quest = questMapper.toQuest(questRequestDto);
        quest.setCreator(creator);
        quest.setBadge(badge);

        assertEquals(quest.getTitle(),questRequestDto.getTitle());
        assertEquals(quest.getCreator().getUid(),questRequestDto.getCreator());
        assertEquals(quest.getBadge().getUid(),questRequestDto.getBadge());
    }

    @Test
    void updateQuestFromDto_shouldUpdateOnlyNotNullFields() {
        QuestRequestToUpdateDto updateDto = new QuestRequestToUpdateDto();

        updateDto.setDescription("Updated Description");
        updateDto.setRewardPoints(10);

        updateDto.setDifficulty(null);
        updateDto.setMaxAttempts(null);

        questMapper.updateQuestFromDto(updateDto, quest);

        assertEquals("Updated Description", quest.getDescription());
        assertEquals(10, quest.getRewardPoints());
        assertEquals(QuestsDifficulty.MEDIUM, quest.getDifficulty());
        assertEquals(2, quest.getMaxAttempts());
        assertEquals("test-title", quest.getTitle());
        assertEquals(creator.getUid(), quest.getCreator().getUid());
    }

}
