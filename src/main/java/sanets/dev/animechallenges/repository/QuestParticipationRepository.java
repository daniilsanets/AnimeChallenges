package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sanets.dev.animechallenges.model.QuestParticipation;
import sanets.dev.animechallenges.model.QuestStatus;
import sanets.dev.animechallenges.model.User;

import java.util.UUID;
@Repository
public interface QuestParticipationRepository extends JpaRepository<QuestParticipation, UUID> {
    Long countByPerformerAndQuestStatus(User performer,  QuestStatus questStatus);
}
