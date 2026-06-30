package com.tara.western;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.auth.AuthService;
import com.tara.common.ApiResponse;
import com.tara.common.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/charts/western") @RequiredArgsConstructor
public class WesternChartController {
    private final WesternChartRepository westernChartRepository;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<WesternChartDto>> getWesternChart() {
        WesternChart chart = westernChartRepository.findPrimaryByUserId(authService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Western chart not found. Please complete onboarding."));
        return ResponseEntity.ok(ApiResponse.success(toDto(chart)));
    }

    private WesternChartDto toDto(WesternChart c) {
        WesternChartDto d = new WesternChartDto();
        d.setId(c.getId().toString()); d.setSunSign(c.getSunSign()); d.setMoonSign(c.getMoonSign());
        d.setAscendant(c.getAscendant()); d.setGeneratedAt(c.getGeneratedAt().toString());
        try { if (c.getPlanetPositions() != null) d.setPlanetPositions(objectMapper.readValue(c.getPlanetPositions(), Object.class));
              if (c.getHouses() != null) d.setHouses(objectMapper.readValue(c.getHouses(), Object.class)); } catch (Exception ignored) {}
        return d;
    }
}
