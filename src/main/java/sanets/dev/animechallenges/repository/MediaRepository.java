package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sanets.dev.animechallenges.model.Media;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    Optional<Media> findByUid(UUID uid);
}
