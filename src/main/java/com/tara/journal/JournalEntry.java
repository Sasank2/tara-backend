package com.tara.journal;
import com.tara.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "journal_entries")
@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class JournalEntry {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "entry_date", nullable = false) @Builder.Default private LocalDate entryDate = LocalDate.now();
    @Column(columnDefinition = "TEXT") private String prompt;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    private String mood;
    @Column(name = "stress_level") private String stressLevel;
    @Column(name = "energy_level") private String energyLevel;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private Instant updatedAt;
}
