package sanets.dev.animechallenges.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.util.stereotypes.Immutable;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="badge")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uid", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private UUID uid;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    @NotNull
    @Length(max = 100)
    private String code;

    @Column(name = "title", nullable = false, length = 200)
    @NotNull
    @Length(max = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", length = 5000)
    @Length(max = 5000)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_media_uid", referencedColumnName = "uid", nullable = false)
    @NotNull
    private Media image;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="rule", nullable = false, columnDefinition="jsonb")
    @NotNull
    private Map<String, Object> rule;

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private OffsetDateTime updatedAt;
}
