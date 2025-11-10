package sanets.dev.animechallenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid", updatable = false, nullable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private UUID uid;

    @Email
    @Column(name = "email", unique = true, nullable = false, length = 320)
    @NotNull
    @Length(max = 320)
    private String email;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    @NotNull
    @Length(max = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 200)
    @NotNull
    @Length(max = 200)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "user_roles")
    @NotNull
    private UserRole role;

    @Column(name = "nickname", length = 200)
    @Length(max = 200)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_media_uid", referencedColumnName = "uid")
    private Media avatar;

    @Column(name = "bio", columnDefinition = "TEXT", length = 1000)
    @Length(max = 1000)
    private String bio;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @NotNull
    private OffsetDateTime updatedAt;

}
