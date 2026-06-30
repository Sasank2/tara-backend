package com.tara.western;
import com.tara.profile.BirthProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "western_charts")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class WesternChart {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "birth_profile_id", nullable = false, unique = true)
    private BirthProfile birthProfile;
    @Column(name = "sun_sign") private String sunSign;
    @Column(name = "moon_sign") private String moonSign;
    private String ascendant;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "planet_positions", columnDefinition = "jsonb") private String planetPositions;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String houses;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String aspects;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "chart_json", columnDefinition = "jsonb") private String chartJson;
    @CreationTimestamp @Column(name = "generated_at", updatable = false) private Instant generatedAt;
}
