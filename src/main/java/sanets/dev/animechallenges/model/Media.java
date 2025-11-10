package sanets.dev.animechallenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
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
    @NotNull
    private UUID uid;

    @Column(name="storage_key", nullable=false)
    @NotNull
    private String storageKey;

    @Column(name="url", nullable=false)
    @NotNull
    private String url;

    @Column(name="mime", nullable=false, length=100)
    @NotNull
    @Length(max = 100)
    private String mimeType;

    @Min(1)
    @Column(name="size", nullable=false)
    @NotNull
    private Long size;

    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false)
    @Setter(AccessLevel.NONE)
    @NotNull
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at", nullable=false)
    @NotNull
    private OffsetDateTime updatedAt;
}
