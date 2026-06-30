package com.tara.home;
import com.tara.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/home") @RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<ApiResponse<HomeDto.HomeResponse>> getHome() {
        return ResponseEntity.ok(ApiResponse.success(homeService.getHome()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<HomeDto.HomeResponse>> refreshGuidance() {
        return ResponseEntity.ok(ApiResponse.success("Guidance refreshed", homeService.refreshGuidance()));
    }
}
