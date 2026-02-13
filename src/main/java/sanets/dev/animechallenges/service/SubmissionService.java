package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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
@Slf4j
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
            log.debug("user attached media files to submission {}", submission);
            addMediaToSubmission(dto.getMedia(), submission);
        }

        log.info("submission created {}", submission);
        return submissionMapper.toSubmissionResponseDto(submissionRepository.save(submission));
    }

    public SubmissionResponseDto rejectSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUid(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setRejectedAt(now());
        submission.setSubmissionStatus(SubmissionStatus.REJECTED);

        submissionRepository.saveAndFlush(submission);
        log.info("submission was rejected {}",  submission.getUid());
        return submissionMapper.toSubmissionResponseDto(submission);
    }

    public SubmissionResponseDto approveSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUid(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setApprovedAt(now());
        submission.setSubmissionStatus(SubmissionStatus.APPROVED);

        submissionRepository.saveAndFlush(submission);
        log.info("submission was approved {}",  submission.getUid());
        return submissionMapper.toSubmissionResponseDto(submission);
    }

    public Submission getSubmissionByUid(UUID submissionUid){
        return submissionRepository.findByUid(submissionUid)
                .orElseThrow(() -> new SubmissionNotFoundException(SUBMISSION_NOT_FOUND_MSG));
    }

    private void addMediaToSubmission(List<Media> mediaList, Submission submission){
        long existingCount = submissionMediaRepository.countBySubmissionUid(submission.getUid());

        if (existingCount + mediaList.size() > MAX_MEDIA_COULD_BE_ADDED) {
            log.error("submission has not been added to media collection, limit {} media", MAX_MEDIA_COULD_BE_ADDED);
            throw new SubmissionMediaLimitExceededException(SUBMISSION_MEDIA_LIMIT_EXCEEDED_MSG.concat(String.valueOf(MAX_MEDIA_COULD_BE_ADDED)));
        }

        mediaList.forEach( media -> submissionMediaRepository.save(
                submissionMapper.toSubmissionMedia(
                        media,
                        submission,
                        mapMimeTypeToMediaType(media.getMimeType()))
        ));
        log.info("Media {} was successfully added to submission {}", mediaList.toString(), submission.getUid());
    }

    private void checkApprovedOrRejected(Submission submission) {
        log.debug("checkApprovedOrRejected submission {}", submission.getUid());
        if (!SubmissionStatus.PENDING.equals(submission.getSubmissionStatus())) {
            log.error("submission was finalized {}", submission.getUid());
            throw new SubmissionIsFinalizedException(SUBMISSION_NOT_AVAILABLE_MSG);
        }
    }

}
