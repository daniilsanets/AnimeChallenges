package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionResponseDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;
import sanets.dev.animechallenges.exception.submission.SubmissionHasItsStateException;
import sanets.dev.animechallenges.exception.submission.SubmissionNotFoundException;
import sanets.dev.animechallenges.mapper.SubmissionMapper;
import sanets.dev.animechallenges.model.Media;
import sanets.dev.animechallenges.model.QuestParticipation;
import sanets.dev.animechallenges.model.Submission;
import sanets.dev.animechallenges.model.SubmissionMedia;
import sanets.dev.animechallenges.model.SubmissionType;
import sanets.dev.animechallenges.repository.SubmissionMediaRepository;
import sanets.dev.animechallenges.repository.SubmissionRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_AVAILABLE_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_FOUND_MSG;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionMediaRepository submissionMediaRepository;
    private final QuestParticipationService questParticipationService;
    private final MediaService mediaService;
    private final SubmissionMapper submissionMapper;

    public CreateSubmissionResponseDto createSubmission(CreateSubmissionRequestDto dto) {

        QuestParticipation participation = questParticipationService.getQuestParticipationByUid(dto.getParticipationUid());
        Submission submission = submissionMapper.toSubmission(dto, participation);
        submission.setSubmittedAt(OffsetDateTime.now());

        submissionRepository.save(submission);

        return submissionMapper.toCreateSubmissionResponseDto(submission);
    }

    public Submission getSubmissionByUid(UUID submissionUid){
        return submissionRepository.findByUid(submissionUid)
                .orElseThrow(() -> new SubmissionNotFoundException(SUBMISSION_NOT_FOUND_MSG));
    }

    public SubmissionResponseDto cancelSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUid(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setRejectedAt(OffsetDateTime.now());
        submissionRepository.save(submission);
        //mapping and return SubmissionResponseDto
        return new SubmissionResponseDto();
    }

    public SubmissionResponseDto approveSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUid(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setApprovedAt(OffsetDateTime.now());
        submissionRepository.save(submission);
        //mapping and return SubmissionResponseDto
        return new SubmissionResponseDto();
    }

    /// will have write this method with many media (going to do auto type definition)
    public void addMediaToSubmission(UUID submissionUid, UUID... mediaUid, SubmissionType... type){
        Submission submission = getSubmissionByUid(submissionUid);
        Media media = mediaService.getMediaByUid(mediaUid);

        SubmissionMedia submissionMedia = SubmissionMedia.builder()
                .submission(submission)
                .media(media)
                .build();

        submissionMediaRepository.save(submissionMedia);
        
    }

    private void checkApprovedOrRejected(Submission submission) {
        if (submission.getRejectedAt() == null || submission.getApprovedAt() == null) {
            throw new SubmissionHasItsStateException(SUBMISSION_NOT_AVAILABLE_MSG);
        }
    }
}
