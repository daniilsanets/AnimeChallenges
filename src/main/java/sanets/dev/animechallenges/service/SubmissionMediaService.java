package sanets.dev.animechallenges.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sanets.dev.animechallenges.exception.submission.SubmissionIsFinalizedException;
import sanets.dev.animechallenges.exception.submission.SubmissionMediaLimitExceededException;
import sanets.dev.animechallenges.model.media.Media;
import sanets.dev.animechallenges.model.submission.Submission;
import sanets.dev.animechallenges.model.submission.SubmissionMedia;
import sanets.dev.animechallenges.model.submission.SubmissionStatus;
import sanets.dev.animechallenges.repository.SubmissionMediaRepository;

import java.util.List;

import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_MEDIA_LIMIT_EXCEEDED_MSG;
import static sanets.dev.animechallenges.exception.ErrorMessages.SUBMISSION_NOT_AVAILABLE_MSG;
import static sanets.dev.animechallenges.mapper.MediaMapper.mapMimeTypeToMediaType;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionMediaService {

    @Value("${media.max-uploaded-media}")
    private int MAX_UPLOADED_MEDIA;

    private final SubmissionMediaRepository submissionMediaRepository;

    @Transactional
    public void attachToSubmission(Submission submission, List<Media> mediaList) {

        if (mediaList == null || mediaList.isEmpty()) {
            return;
        }

        if (!SubmissionStatus.PENDING.equals(submission.getSubmissionStatus())) {
            log.error("Attempt to attach media to finalized submission {}", submission.getUid());
            throw new SubmissionIsFinalizedException(SUBMISSION_NOT_AVAILABLE_MSG);
        }

        long existingCount = submissionMediaRepository.countBySubmissionUid(submission.getUid());

        if (existingCount + mediaList.size() > MAX_UPLOADED_MEDIA) {
            log.error("Media limit exceeded: existing={}, new={}, max={}",
                    existingCount, mediaList.size(), MAX_UPLOADED_MEDIA);
            throw new SubmissionMediaLimitExceededException(
                    SUBMISSION_MEDIA_LIMIT_EXCEEDED_MSG + MAX_UPLOADED_MEDIA
            );
        }

        for (Media media : mediaList) {
            SubmissionMedia submissionMedia = SubmissionMedia.builder()
                    .submission(submission)
                    .media(media)
                    .mediaType(mapMimeTypeToMediaType(media.getMimeType()))
                    .build();

            submissionMediaRepository.save(submissionMedia);
        }

        log.info("Attached {} media files to submission {}", mediaList.size(), submission.getUid());
    }
}
