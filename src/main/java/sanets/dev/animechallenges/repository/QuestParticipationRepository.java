package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sanets.dev.animechallenges.model.quest.Quest;
import sanets.dev.animechallenges.model.quest.QuestParticipation;
import sanets.dev.animechallenges.model.quest.QuestStatus;
import sanets.dev.animechallenges.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface QuestParticipationRepository extends JpaRepository<QuestParticipation, UUID> {
    Long countByPerformerAndQuestStatus(User performer,  QuestStatus questStatus);
    Optional<QuestParticipation> findQuestParticipationByUid(UUID questParticipation);

    List<QuestParticipation> findAllByPerformerUid(UUID performerUid);

    boolean existsByPerformerAndQuest(User performer, Quest quest);

    @Query("SELECT qp FROM QuestParticipation qp JOIN FETCH qp.quest WHERE qp.performer.uid = :userUid")
    List<QuestParticipation> findWithQuestByPerformerUid(@Param("userUid") UUID userUid);
}
