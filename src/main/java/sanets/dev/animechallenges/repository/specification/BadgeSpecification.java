package sanets.dev.animechallenges.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import sanets.dev.animechallenges.model.badge.Badge;

public class BadgeSpecification {
    private BadgeSpecification() {}

    public static Specification<Badge> nameContains(String name){
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Badge> isActive(Boolean isActive){
        return (root, query, cb) -> {
            if (isActive == null) {
                return null;
            }
            return cb.equal(root.get("isActive"), isActive);
        };
    }
}
