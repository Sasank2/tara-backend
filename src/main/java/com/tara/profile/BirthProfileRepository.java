package com.tara.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BirthProfileRepository extends JpaRepository<BirthProfile, UUID> {

    @Query("SELECT bp FROM BirthProfile bp WHERE bp.user.id = :userId AND bp.isPrimary = true")
    Optional<BirthProfile> findPrimaryByUserId(@Param("userId") UUID userId);

    boolean existsByUserId(UUID userId);
}
