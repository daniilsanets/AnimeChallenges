package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import sanets.dev.animechallenges.model.Quest;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestRepository extends JpaRepository<Quest, UUID>, JpaSpecificationExecutor<Quest> {
    Optional<Quest> findByUid(UUID uid);
}
