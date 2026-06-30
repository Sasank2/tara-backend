package com.tara.home;
import com.tara.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "daily_guidance")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class DailyGuidance {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "guidance_date", nullable = false) @Builder.Default private LocalDate guidanceDate = LocalDate.now();
    private String energy;
    @Column(name = "focus_area") private String focusArea;
    @Column(name = "what_today_means", columnDefinition = "TEXT") private String whatTodayMeans;
    @Column(name = "focus_guidance", columnDefinition = "TEXT") private String focusGuidance;
    @Column(name = "avoid_guidance", columnDefinition = "TEXT") private String avoidGuidance;
    @Column(name = "favorable_time") private String favorableTime;
    @Column(name = "wellness_action", columnDefinition = "TEXT") private String wellnessAction;
    @Column(name = "practical_step", columnDefinition = "TEXT") private String practicalStep;
    @Column(name = "reflection_prompt", columnDefinition = "TEXT") private String reflectionPrompt;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "astrology_snapshot", columnDefinition = "jsonb") private String astrologySnapshot;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;
}
