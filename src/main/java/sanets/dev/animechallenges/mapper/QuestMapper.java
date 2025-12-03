package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.quest.QuestRequestDto;
import sanets.dev.animechallenges.dto.quest.QuestRequestToUpdateDto;
import sanets.dev.animechallenges.model.Quest;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface QuestMapper {

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
    Quest toQuest(QuestRequestDto questRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "badge", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateQuestFromDto(QuestRequestToUpdateDto dto, @MappingTarget Quest quest);
}
