package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.quest.QuestResponseDto;
import sanets.dev.animechallenges.dto.quest.CreateQuestRequestDto;
import sanets.dev.animechallenges.dto.quest.UpdateQuestRequestDto;
import sanets.dev.animechallenges.model.Quest;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface QuestMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "badge", "creator"
    })
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "difficulty", target = "difficulty")
    @Mapping(source = "rewardPoints", target = "rewardPoints")
    @Mapping(source = "maxAttempts", target = "maxAttempts")

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "badge", ignore = true)
    @Mapping(target = "uid" , ignore = true)
    @Mapping(target = "isActive" , ignore = true)
    @Mapping(target = "createdAt" , ignore = true)
    @Mapping(target = "updatedAt" , ignore = true)
    Quest toQuest(CreateQuestRequestDto createQuestRequestDto);

    //or I can set up like SET_TO_NULL but it will be a full update instead of partly
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "badge", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateQuestFromDto(UpdateQuestRequestDto dto, @MappingTarget Quest quest);

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "createdAt","updatedAt"
    })
    @Mapping(source = "uid", target ="uid")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "difficulty", target = "difficulty")
    @Mapping(source = "rewardPoints", target = "rewardPoints")
    @Mapping(source = "maxAttempts", target = "maxAttempts")
    @Mapping(source = "badge.uid", target = "badgeUid")
    @Mapping(source = "creator.uid", target = "creatorUid")

    QuestResponseDto toQuestResponseDto(Quest quest);
}
