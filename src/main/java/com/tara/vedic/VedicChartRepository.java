package com.tara.vedic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VedicChartRepository extends JpaRepository<VedicChart, UUID> {
    @Query("SELECT vc FROM VedicChart vc WHERE vc.birthProfile.id = :id")
    Optional<VedicChart> findByBirthProfileId(@Param("id") UUID id);
    @Query("SELECT vc FROM VedicChart vc WHERE vc.birthProfile.user.id = :userId AND vc.birthProfile.isPrimary = true")
    Optional<VedicChart> findPrimaryByUserId(@Param("userId") UUID userId);
}
