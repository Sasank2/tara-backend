package com.tara.home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyGuidanceRepository extends JpaRepository<DailyGuidance, UUID> {
    Optional<DailyGuidance> findByUserIdAndGuidanceDate(UUID userId, LocalDate date);
}
