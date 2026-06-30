package com.tara.vedic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.auth.AuthService;
import com.tara.common.ApiResponse;
import com.tara.common.Exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/charts/vedic") @RequiredArgsConstructor
public class VedicChartController {
    private final VedicChartRepository vedicChartRepository;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<VedicChartDto>> getVedicChart() {
        VedicChart chart = vedicChartRepository.findPrimaryByUserId(authService.getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vedic chart not found. Please complete onboarding."));
        return ResponseEntity.ok(ApiResponse.success(toDto(chart)));
    }

    private VedicChartDto toDto(VedicChart c) {
        VedicChartDto d = new VedicChartDto();
        d.setId(c.getId().toString()); d.setLagna(c.getLagna()); d.setRashi(c.getRashi());
        d.setNakshatra(c.getNakshatra()); d.setGeneratedAt(c.getGeneratedAt().toString());
        try { if (c.getDasha() != null) d.setDasha(objectMapper.readValue(c.getDasha(), Object.class));
              if (c.getPlanetPositions() != null) d.setPlanetPositions(objectMapper.readValue(c.getPlanetPositions(), Object.class)); } catch (Exception ignored) {}
        return d;
    }
}
