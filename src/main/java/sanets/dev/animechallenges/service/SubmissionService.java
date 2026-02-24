package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sanets.dev.animechallenges.dto.submission.CreateSubmissionRequestDto;
import sanets.dev.animechallenges.dto.submission.SubmissionResponseDto;
import sanets.dev.animechallenges.event.QuestApprovedEvent;
import sanets.dev.animechallenges.exception.submission.SubmissionIsFinalizedException;
import sanets.dev.animechallenges.exception.submission.SubmissionNotFoundException;
import sanets.dev.animechallenges.mapper.SubmissionMapper;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.quest.QuestParticipation;
import sanets.dev.animechallenges.model.submission.Submission;
import sanets.dev.animechallenges.model.submission.SubmissionStatus;
import sanets.dev.animechallenges.repository.SubmissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.OffsetDateTime.now;
import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_AVAILABLE_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_FOUND_MSG;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionMediaService submissionMediaService;
    private final QuestParticipationService questParticipationService;
    private final SubmissionMapper submissionMapper;
    private final MediaService mediaService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SubmissionResponseDto createSubmission(CreateSubmissionRequestDto dto) {

        QuestParticipation participation =
                questParticipationService.getQuestParticipationByUid(dto.getParticipationUid());
        Submission submission = submissionMapper.toSubmission(dto, participation);
        submission.setSubmittedAt(now());

        submission = submissionRepository.save(submission);

        List<Media> uploadedMedia = new ArrayList<>();

        if (!dto.getMultipartFiles().isEmpty()) {
            for (MultipartFile file : dto.getMultipartFiles()) {
                uploadedMedia.add(mediaService.upload(file));
            }
        }

        submissionMediaService.attachToSubmission(submission, uploadedMedia);

        log.info("submission created {}", submission);
        return submissionMapper.toSubmissionResponseDto(submission);
    }

    public SubmissionResponseDto rejectSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUidOrThrow(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setRejectedAt(now());
        submission.setSubmissionStatus(SubmissionStatus.REJECTED);

        submissionRepository.saveAndFlush(submission);
        log.info("submission was rejected {}",  submission.getUid());
        return submissionMapper.toSubmissionResponseDto(submission);
    }

    public SubmissionResponseDto approveSubmissionByUid(UUID submissionUid){
        Submission submission = getSubmissionByUidOrThrow(submissionUid);

        checkApprovedOrRejected(submission);

        submission.setApprovedAt(now());
        submission.setSubmissionStatus(SubmissionStatus.APPROVED);

        submissionRepository.saveAndFlush(submission);

        eventPublisher.publishEvent( new QuestApprovedEvent(
                this,
                submission.getQuestParticipation().getPerformer().getUid(),
                submission.getQuestParticipation().getQuest()
        ));

        log.info("submission was approved {}",  submission.getUid());
        return submissionMapper.toSubmissionResponseDto(submission);
    }

    public SubmissionResponseDto getSubmissionResponseDto(UUID submissionUid){
        return submissionMapper.toSubmissionResponseDto(getSubmissionByUidOrThrow(submissionUid));
    }

    private Submission getSubmissionByUidOrThrow(UUID submissionUid){
        return submissionRepository.findByUid(submissionUid)
                .orElseThrow(() -> new SubmissionNotFoundException(SUBMISSION_NOT_FOUND_MSG));
    }

    private void checkApprovedOrRejected(Submission submission) {
        log.debug("checkApprovedOrRejected submission {}", submission.getUid());
        if (!SubmissionStatus.PENDING.equals(submission.getSubmissionStatus())) {
            log.error("submission was finalized {}", submission.getUid());
            throw new SubmissionIsFinalizedException(SUBMISSION_NOT_AVAILABLE_MSG);
        }
    }

}
