package sanets.dev.animechallenges.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import sanets.dev.animechallenges.model.quest.Quest;
import sanets.dev.animechallenges.model.quest.QuestsDifficulty;

public class QuestSpecification {
    private QuestSpecification(){}

    public static Specification<Quest> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Quest> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return null;
            }
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Quest> hasDifficulty(QuestsDifficulty difficulty) {
        return (root, query, cb) -> {
            if (difficulty == null) {
                return null;
            }
            return cb.equal(root.get("difficulty"), difficulty);
        };
    }

    public static Specification<Quest> hasRewardPoints(Integer points) {
        return (root, query, cb) -> {
            if (points == null) {
                return null;
            }
            return cb.equal(root.get("rewardPoints"), points);
        };
    }

    public static Specification<Quest> hasMaxAttempts(Integer attempts) {
        return (root, query, cb) -> {
            if (attempts == null) {
                return null;
            }
            return cb.equal(root.get("maxAttempts"), attempts);
        };
    }
}
