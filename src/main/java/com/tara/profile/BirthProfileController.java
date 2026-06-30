package com.tara.profile;

import com.tara.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class BirthProfileController {

    private final BirthProfileService birthProfileService;

    /**
     * POST /api/profile/birth
     * Creates birth profile, resolves location via GeoNames, triggers chart generation.
     */
    @PostMapping("/birth")
    public ResponseEntity<ApiResponse<BirthProfileDto.Response>> createBirthProfile(
            @Valid @RequestBody BirthProfileDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Birth profile created", birthProfileService.createBirthProfile(request)));
    }


    /**
     * PUT /api/profile/birth
     * Updates birth profile, re-resolves location if changed, regenerates charts.
     */
    @PutMapping("/birth")
    public ResponseEntity<ApiResponse<BirthProfileDto.Response>> updateBirthProfile(
            @Valid @RequestBody BirthProfileDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Birth profile updated", birthProfileService.updateBirthProfile(request)));
    }

    /**
     * GET /api/profile/birth
     */
    @GetMapping("/birth")
    public ResponseEntity<ApiResponse<BirthProfileDto.Response>> getBirthProfile() {
        return ResponseEntity.ok(ApiResponse.success(birthProfileService.getBirthProfile()));
    }
}
