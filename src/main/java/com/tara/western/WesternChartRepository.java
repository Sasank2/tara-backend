package com.tara.western;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WesternChartRepository extends JpaRepository<WesternChart, UUID> {
    @Query("SELECT wc FROM WesternChart wc WHERE wc.birthProfile.id = :id")
    Optional<WesternChart> findByBirthProfileId(@Param("id") UUID id);
    @Query("SELECT wc FROM WesternChart wc WHERE wc.birthProfile.user.id = :userId AND wc.birthProfile.isPrimary = true")
    Optional<WesternChart> findPrimaryByUserId(@Param("userId") UUID userId);
}
