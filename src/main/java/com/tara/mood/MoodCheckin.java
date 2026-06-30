package com.tara.mood;
import com.tara.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "mood_checkins")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class MoodCheckin {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false) private String mood;
    @Column(name = "stress_level") private String stressLevel;
    @Column(name = "energy_level") private String energyLevel;
    @Column(name = "sleep_quality") private String sleepQuality;
    private String note;
    @Column(name = "checkin_date") @Builder.Default private LocalDate checkinDate = LocalDate.now();
    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;
}
