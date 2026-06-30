package com.tara.notification;
import com.google.firebase.messaging.*;
import com.tara.auth.AuthService;
import com.tara.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j @Service @RequiredArgsConstructor
public class NotificationService {
    private final FcmTokenRepository fcmTokenRepository;
    private final AuthService authService;
    @Value("${fcm.enabled}") private boolean fcmEnabled;

    @Transactional
    public void registerToken(String token, String deviceType) {
        User user = authService.getCurrentUser();
        fcmTokenRepository.findByToken(token).orElseGet(() -> fcmTokenRepository.save(
            FcmToken.builder().user(user).token(token).deviceType(deviceType).build()));
    }

    @Transactional
    public void removeToken(String token) { fcmTokenRepository.deleteByToken(token); }

    public void sendDailyGuidanceNotification(UUID userId) {
        if (!fcmEnabled) return;
        sendToUser(userId, "Your daily guidance is ready ✨", "Tap to see what today holds.", "daily_guidance");
    }

    private void sendToUser(UUID userId, String title, String body, String type) {
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(userId);
        for (FcmToken t : tokens) {
            try {
                FirebaseMessaging.getInstance().send(Message.builder().setToken(t.getToken())
                        .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                        .putData("type", type).build());
            } catch (FirebaseMessagingException e) {
                log.warn("FCM send failed: {}", e.getMessage());
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) fcmTokenRepository.delete(t);
            }
        }
    }
}
