package com.tara.journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends JpaRepository<JournalEntry, UUID> {
    Page<JournalEntry> findByUserIdOrderByEntryDateDesc(UUID userId, Pageable pageable);
    Optional<JournalEntry> findByIdAndUserId(UUID id, UUID userId);
}
