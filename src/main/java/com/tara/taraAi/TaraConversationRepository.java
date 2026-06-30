package com.tara.taraAi;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaraConversationRepository extends JpaRepository<TaraConversation, UUID> {
    List<TaraConversation> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    @Query("SELECT tc FROM TaraConversation tc WHERE tc.user.id = :userId AND tc.isSaved = true ORDER BY tc.createdAt DESC")
    List<TaraConversation> findSavedByUserId(@Param("userId") UUID userId);
    Optional<TaraConversation> findByIdAndUserId(UUID id, UUID userId);
}
