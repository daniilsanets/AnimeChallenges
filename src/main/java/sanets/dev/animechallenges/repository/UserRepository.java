package sanets.dev.animechallenges.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sanets.dev.animechallenges.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUid(UUID uid);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUid(UUID uid);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
