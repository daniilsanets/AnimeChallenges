package sanets.dev.animechallenges.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.IdClass;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Класс, описывающий композитный ключ
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserBadgeId implements Serializable {
    private UUID user;
    private UUID badge;
}