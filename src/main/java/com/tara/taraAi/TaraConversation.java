package com.tara.taraAi;
import com.tara.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "tara_conversations")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class TaraConversation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    private String category;
    @Column(nullable = false, columnDefinition = "TEXT") private String question;
    @Column(name = "short_answer", columnDefinition = "TEXT") private String shortAnswer;
    @Column(name = "why_tara_says", columnDefinition = "TEXT") private String whyTaraSays;
    @Column(name = "practical_step", columnDefinition = "TEXT") private String practicalStep;
    @Column(name = "wellness_suggestion", columnDefinition = "TEXT") private String wellnessSuggestion;
    @Column(name = "reflection_question", columnDefinition = "TEXT") private String reflectionQuestion;
    @Column(name = "is_saved") @Builder.Default private boolean isSaved = false;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;
}
