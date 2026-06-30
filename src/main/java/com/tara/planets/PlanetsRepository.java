package com.tara.planets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanetsRepository extends JpaRepository<DailyPlanets, UUID> {
    Optional<DailyPlanets> findByPlanetDate(LocalDate date);
    boolean existsByPlanetDate(LocalDate date);
}
