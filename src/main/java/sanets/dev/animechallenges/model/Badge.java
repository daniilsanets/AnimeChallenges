package sanets.dev.animechallenges.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

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
    private UUID uid;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_media_uid", referencedColumnName = "uid", nullable = false)
    private Media image;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="rule", nullable = false, columnDefinition="jsonb")
    private Map<String, Object> rule;

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private OffsetDateTime updatedAt;
}
