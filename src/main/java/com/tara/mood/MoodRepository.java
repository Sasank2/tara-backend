package com.tara.mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MoodRepository extends JpaRepository<MoodCheckin, UUID> {
    Optional<MoodCheckin> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<MoodCheckin> findByUserIdAndCheckinDate(UUID userId, LocalDate date);
    @Query("SELECT m FROM MoodCheckin m WHERE m.user.id = :userId AND m.checkinDate >= :from ORDER BY m.checkinDate DESC")
    List<MoodCheckin> findRecentByUserId(@Param("userId") UUID userId, @Param("from") LocalDate from);
}
