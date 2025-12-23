package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sanets.dev.animechallenges.model.SubmissionMedia;

import java.util.UUID;

public interface SubmissionMediaRepository extends JpaRepository<SubmissionMedia, UUID> {
}
