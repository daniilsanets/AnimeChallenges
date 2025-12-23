package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionResponseDto;
import sanets.dev.animechallenges.exception.submission.SubmissionNotFoundException;
import sanets.dev.animechallenges.mapper.SubmissionMapper;
import sanets.dev.animechallenges.model.QuestParticipation;
import sanets.dev.animechallenges.model.Submission;
import sanets.dev.animechallenges.repository.SubmissionMediaRepository;
import sanets.dev.animechallenges.repository.SubmissionRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_FOUND_MSG;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionMediaRepository submissionMediaRepository;
    private final QuestParticipationService questParticipationService;
    private final SubmissionMapper submissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CreateSubmissionResponseDto createSubmission(CreateSubmissionRequestDto dto) {

        QuestParticipation participation = questParticipationService.getQuestParticipationByUid(dto.getParticipationUid());
        Submission submission = submissionMapper.toSubmission(dto, participation);
        submission.setSubmittedAt(OffsetDateTime.now());

        submissionRepository.save(submission);

        return submissionMapper.toCreateSubmissionResponseDto(submission);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Submission getSubmissionByUid(UUID submissionUid){
        return submissionRepository.findByUid(submissionUid)
                .orElseThrow(() -> new SubmissionNotFoundException(SUBMISSION_NOT_FOUND_MSG));
    }

    /// In general you should set time of ending submission (rejected or approvedAt) 
}
