package sanets.dev.animechallenges.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.media.MediaType;
import sanets.dev.animechallenges.model.quest.QuestParticipation;
import sanets.dev.animechallenges.model.submission.Submission;
import sanets.dev.animechallenges.model.submission.SubmissionMedia;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SubmissionMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {
        "participationUid", "media",  "uid" , "performer", "quest", "questStatus", "startedAt", "score"
    })
    @Mapping(source = "participation", target = "questParticipation")
    @Mapping(target = "uid", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "rejectedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    Submission toSubmission(CreateSubmissionRequestDto dto, QuestParticipation participation);

    @Mapping(target = "createdAt", ignore = true)
    SubmissionMedia toSubmissionMedia(Media media, Submission submission, MediaType mediaType);

    @BeanMapping(ignoreUnmappedSourceProperties = {
            "submittedAt", "rejectedAt", "approvedAt", "createdAt", "updatedAt"
    })
    @Mapping(source = "submission.questParticipation.uid", target = "participationUid")
    SubmissionResponseDto toSubmissionResponseDto(Submission submission);
}
