package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;
import sanets.dev.animechallenges.exception.submission.SubmissionIsFinalizedException;
import sanets.dev.animechallenges.exception.submission.SubmissionMediaLimitExceededException;
import sanets.dev.animechallenges.exception.submission.SubmissionNotFoundException;
import sanets.dev.animechallenges.mapper.SubmissionMapper;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.quest.QuestParticipation;
import sanets.dev.animechallenges.model.submission.Submission;
import sanets.dev.animechallenges.model.submission.SubmissionStatus;
import sanets.dev.animechallenges.repository.SubmissionMediaRepository;
import sanets.dev.animechallenges.repository.SubmissionRepository;

import java.util.List;
import java.util.UUID;

import static java.time.OffsetDateTime.now;
import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_MEDIA_LIMIT_EXCEEDED_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_AVAILABLE_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_FOUND_MSG;
import static sanets.dev.animechallenges.mapper.MediaMapper.mapMimeTypeToMediaType;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    @Value("${media.max-uploaded-media}")
    private int MAX_MEDIA_COULD_BE_ADDED;

    private final SubmissionRepository submissionRepository;
    private final SubmissionMediaRepository submissionMediaRepository;
    private final QuestParticipationService questParticipationService;
    private final SubmissionMapper submissionMapper;

    @Transactional
    public SubmissionResponseDto createSubmission(CreateSubmissionRequestDto dto) {

        QuestParticipation participation = questParticipationService.getQuestParticipationByUid(dto.getParticipationUid());
        Submission submission = submissionMapper.toSubmission(dto, participation);
        submission.setSubmittedAt(now());

        if (!dto.getMedia().isEmpty()) {
            addMediaToSubmission(dto.getMedia(), submission);
        }

        return submissionMapper.toSubmissionResponseDto(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionResponseDto rejectSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUid(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setRejectedAt(now());
        submission.setSubmissionStatus(SubmissionStatus.REJECTED);
        submissionRepository.saveAndFlush(submission);

        return submissionMapper.toSubmissionResponseDto(submission);
    }

    @Transactional
    public SubmissionResponseDto approveSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUid(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setApprovedAt(now());
        submission.setSubmissionStatus(SubmissionStatus.APPROVED);
        submissionRepository.saveAndFlush(submission);

        return submissionMapper.toSubmissionResponseDto(submission);
    }

    public Submission getSubmissionByUid(UUID submissionUid){
        return submissionRepository.findByUid(submissionUid)
                .orElseThrow(() -> new SubmissionNotFoundException(SUBMISSION_NOT_FOUND_MSG));
    }

    private void addMediaToSubmission(List<Media> mediaList, Submission submission){
        long existingCount = submissionMediaRepository.countBySubmissionUid(submission.getUid());

        if (existingCount + mediaList.size() > MAX_MEDIA_COULD_BE_ADDED) {
            throw new SubmissionMediaLimitExceededException(SUBMISSION_MEDIA_LIMIT_EXCEEDED_MSG.concat(String.valueOf(MAX_MEDIA_COULD_BE_ADDED)));
        }

        mediaList.forEach( media -> submissionMediaRepository.save(
                submissionMapper.toSubmissionMedia(
                        media,
                        submission,
                        mapMimeTypeToMediaType(media.getMimeType()))
        ));
    }

    private void checkApprovedOrRejected(Submission submission) {
        if (!SubmissionStatus.PENDING.equals(submission.getSubmissionStatus())) {
            throw new SubmissionIsFinalizedException(SUBMISSION_NOT_AVAILABLE_MSG);
        }
    }

}
