package com.tara.notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, UUID> {
    List<FcmToken> findByUserId(UUID userId);
    Optional<FcmToken> findByToken(String token);
    void deleteByToken(String token);
}
