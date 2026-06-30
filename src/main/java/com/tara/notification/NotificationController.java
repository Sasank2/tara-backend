package com.tara.notification;
import com.tara.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/notifications") @RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<Void>> registerToken(@Valid @RequestBody TokenRequest req) {
        notificationService.registerToken(req.getToken(), req.getDeviceType());
        return ResponseEntity.ok(ApiResponse.success("Token registered", null));
    }

    @DeleteMapping("/token")
    public ResponseEntity<ApiResponse<Void>> removeToken(@Valid @RequestBody TokenRequest req) {
        notificationService.removeToken(req.getToken());
        return ResponseEntity.ok(ApiResponse.success("Token removed", null));
    }

    @Data
    public static class TokenRequest {
        @NotBlank(message = "Token is required") private String token;
        private String deviceType;
    }
}
