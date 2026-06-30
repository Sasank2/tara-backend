package com.tara.vedic;
import com.tara.profile.BirthProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "vedic_charts")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class VedicChart {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "birth_profile_id", nullable = false, unique = true)
    private BirthProfile birthProfile;
    private String lagna;
    private String rashi;
    private String nakshatra;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String dasha;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "planet_positions", columnDefinition = "jsonb") private String planetPositions;
    @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition = "jsonb") private String panchang;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "chart_json", columnDefinition = "jsonb") private String chartJson;
    @CreationTimestamp @Column(name = "generated_at", updatable = false) private Instant generatedAt;
}
