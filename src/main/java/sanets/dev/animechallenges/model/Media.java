package sanets.dev.animechallenges.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name="media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="uid", updatable=false, nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID uid;

    @Column(name="storage_key", nullable=false)
    private String storageKey;

    @Column(name="url", nullable=false)
    private String url;

    @Column(name="mime", nullable=false, length=100)
    private String mimeType;

    @Min(1)
    @Column(name="size", nullable=false)
    private Long size;

    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false)
    @Setter(AccessLevel.NONE)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at", nullable=false)
    private OffsetDateTime updatedAt;
}
