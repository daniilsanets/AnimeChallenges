package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionResponseDto;
import sanets.dev.animechallenges.model.QuestParticipation;
import sanets.dev.animechallenges.model.Submission;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SubmissionMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
        "participationUid"
    })
    @Mapping(source = "participation", target = "questParticipation")
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "rejectedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    Submission toSubmission(CreateSubmissionRequestDto dto, QuestParticipation participation);

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "submissionType", "submittedAt", "rejectedAt", "approvedAt"
    })

    @Mapping(source = "submission.questParticipation.uid", target = "participationUid")
    CreateSubmissionResponseDto toCreateSubmissionResponseDto(Submission submission);
}
