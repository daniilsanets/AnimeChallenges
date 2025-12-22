package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.participation.UpdateQuestParticipationRequestDto;
import sanets.dev.animechallenges.dto.participation.QuestParticipationResponseDto;
import sanets.dev.animechallenges.model.QuestParticipation;

@Mapper(componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface QuestParticipationMapper {

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            ignoreUnmappedSourceProperties = {
                    "participationUid"
            })
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
}
