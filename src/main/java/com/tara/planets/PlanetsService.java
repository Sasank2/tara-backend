package com.tara.planets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.vedic.AstrologyApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanetsService {

    private final PlanetsRepository planetsRepository;
    private final AstrologyApiClient astrologyApiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public DailyPlanets calculateAndSave(LocalDate date) {
        if (planetsRepository.existsByPlanetDate(date)) {
            log.info("Planets already exist for {}", date);
            return planetsRepository.findByPlanetDate(date).orElseThrow();
        }

        log.info("Fetching planetary positions for: {}", date);
        try {
            List<Map<String, Object>> data = astrologyApiClient.getDailyPlanetaryPositions(date);

            DailyPlanets planets = DailyPlanets.builder()
                    .planetDate(date)
                    .sunPosition(extract(data, "Sun"))
                    .moonPosition(extract(data, "Moon"))
                    .mercuryPosition(extract(data, "Mercury"))
                    .venusPosition(extract(data, "Venus"))
                    .marsPosition(extract(data, "Mars"))
                    .jupiterPosition(extract(data, "Jupiter"))
                    .saturnPosition(extract(data, "Saturn"))
                    .planetJson(objectMapper.writeValueAsString(data))
                    .build();

            DailyPlanets saved = planetsRepository.save(planets);
            log.info("Planets saved for {}: Sun={}, Moon={}",
                    date, saved.getSunPosition(), saved.getMoonPosition());
            return saved;

        } catch (Exception e) {
            log.error("Failed to fetch planetary positions for {}", date, e);
            throw new RuntimeException("Planetary position fetch failed", e);
        }
    }

    @Transactional(readOnly = true)
    public DailyPlanets getTodayPlanets() {
        LocalDate today = LocalDate.now();
        return planetsRepository.findByPlanetDate(today)
                .orElseGet(() -> calculateAndSave(today));
    }

    private String extract(List<Map<String, Object>> data, String planetName) {
        if (data == null) return null;
        for (Map<String, Object> planet : data) {
            if (planetName.equalsIgnoreCase((String) planet.get("name"))) {
                return (String) planet.get("sign");
            }
        }
        return null;
    }
}