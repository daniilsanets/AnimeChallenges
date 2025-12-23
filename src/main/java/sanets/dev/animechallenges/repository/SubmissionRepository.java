package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sanets.dev.animechallenges.model.Submission;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    Optional<Submission> findByUid(UUID id);
}
