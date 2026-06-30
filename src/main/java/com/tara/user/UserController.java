package com.tara.user;

import com.tara.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/user
     * Returns current user profile. Auto-registers on first call.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserDto.Response>> getUser() {
        return ResponseEntity.ok(ApiResponse.success(userService.registerOrGet()));
    }

    /**
     * PATCH /api/user
     * Updates user profile fields.
     */
    @PatchMapping
    public ResponseEntity<ApiResponse<UserDto.Response>> updateUser(
            @Valid @RequestBody UserDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", userService.updateUser(request)));
    }
}
