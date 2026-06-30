package com.tara.planets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/planets") @RequiredArgsConstructor
public class PlanetsController {
    private final PlanetsService planetsService;
    private final ObjectMapper objectMapper;

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<PlanetsDto>> getTodayPlanets() {
        DailyPlanets p = planetsService.getTodayPlanets();
        PlanetsDto dto = new PlanetsDto();
        dto.setDate(p.getPlanetDate().toString());
        dto.setSunPosition(p.getSunPosition()); dto.setMoonPosition(p.getMoonPosition());
        dto.setMercuryPosition(p.getMercuryPosition()); dto.setVenusPosition(p.getVenusPosition());
        dto.setMarsPosition(p.getMarsPosition()); dto.setJupiterPosition(p.getJupiterPosition());
        dto.setSaturnPosition(p.getSaturnPosition());
        try { if (p.getPlanetJson() != null) dto.setPlanetDetails(objectMapper.readValue(p.getPlanetJson(), Object.class)); } catch (Exception ignored) {}
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}
