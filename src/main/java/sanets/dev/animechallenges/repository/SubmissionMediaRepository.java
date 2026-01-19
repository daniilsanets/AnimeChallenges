package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sanets.dev.animechallenges.model.SubmissionMedia;
import sanets.dev.animechallenges.model.SubmissionMediaId;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionMediaRepository extends JpaRepository<SubmissionMedia, UUID> {
    Long countBySubmissionUid(UUID submissionUid);
}
