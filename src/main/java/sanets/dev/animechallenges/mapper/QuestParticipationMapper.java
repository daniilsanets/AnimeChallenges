package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.participation.UpdateQuestParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.QuestParticipationResponseDto;
import sanets.dev.animechallenges.model.quest.Quest;
import sanets.dev.animechallenges.model.quest.QuestParticipation;
import sanets.dev.animechallenges.model.user.User;

@Mapper(componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface QuestParticipationMapper {

    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "performer", ignore = true)
    @Mapping(target = "quest", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateQuestParticipationFromDto(UpdateQuestParticipationRequestDto dto, @MappingTarget QuestParticipation questParticipation);

    @BeanMapping(
            ignoreUnmappedSourceProperties = {
                    "startedAt", "createdAt", "updatedAt"
            }
    )
    @Mapping(source = "performer.uid", target = "userUid")
    @Mapping(source = "quest.uid", target = "questUid")
    QuestParticipationResponseDto toQuestParticipationResponseDto(QuestParticipation questParticipation);

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "email", "username", "passwordHash", "role", "nickname",
            "avatar", "bio", "isActive", "createdAt", "updatedAt", "title", "description", "difficulty",
            "rewardPoints", "badge", "maxAttempts", "creator"
    })
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "questStatus", ignore = true)
    @Mapping(target = "performer", source = "currentUser")
    QuestParticipation toQuestParticipation(User currentUser, Quest quest, Integer score);
}
