package com.tara.planets;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "daily_planets")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class DailyPlanets {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "planet_date", nullable = false, unique = true) private LocalDate planetDate;
    @Column(name = "moon_position") private String moonPosition;
    @Column(name = "mercury_position") private String mercuryPosition;
    @Column(name = "venus_position") private String venusPosition;
    @Column(name = "mars_position") private String marsPosition;
    @Column(name = "jupiter_position") private String jupiterPosition;
    @Column(name = "saturn_position") private String saturnPosition;
    @Column(name = "sun_position") private String sunPosition;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "planet_json", columnDefinition = "jsonb") private String planetJson;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;
}
