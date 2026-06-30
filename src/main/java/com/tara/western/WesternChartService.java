package com.tara.western;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.common.Exceptions.TaraException;
import com.tara.profile.BirthProfile;
import com.tara.vedic.AstrologyApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WesternChartService {

    private final WesternChartRepository westernChartRepository;
    private final AstrologyApiClient astrologyApiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public WesternChart generateChart(BirthProfile profile) {
        log.info("Generating Western chart for profile: {}", profile.getId());
        westernChartRepository.findByBirthProfileId(profile.getId())
                .ifPresent(westernChartRepository::delete);
        westernChartRepository.flush();

        try {
            // western_chart_data returns houses + aspects
            Map<String, Object> chartData = astrologyApiClient.getWesternPlanets(profile);

	    log.info("Raw chart data keys: {}", chartData.keySet());
	    log.info("Raw chart data: {}", chartData);

            String sunSign   = findPlanetSign(chartData, "Sun");
            String moonSign  = findPlanetSign(chartData, "Moon");
            String ascendant = findAscendant(chartData);

            WesternChart chart = WesternChart.builder()
                    .birthProfile(profile)
                    .sunSign(sunSign)
                    .moonSign(moonSign)
                    .ascendant(ascendant)
                    .planetPositions(objectMapper.writeValueAsString(chartData))
                    .houses(objectMapper.writeValueAsString(chartData.get("houses")))
                    .aspects(objectMapper.writeValueAsString(chartData.get("aspects")))
                    .chartJson(objectMapper.writeValueAsString(Map.of(
                            "sunSign",   sunSign   != null ? sunSign   : "",
                            "moonSign",  moonSign  != null ? moonSign  : "",
                            "ascendant", ascendant != null ? ascendant : ""
                    )))
                    .build();

            WesternChart saved = westernChartRepository.save(chart);
            log.info("Western chart saved: Sun={}, Moon={}, Asc={}", sunSign, moonSign, ascendant);
            return saved;

        } catch (Exception e) {
            log.error("Failed to generate Western chart", e);
            throw new TaraException("Failed to generate Western chart",
                    "WESTERN_CHART_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Finds a planet's sign by scanning all planets inside all houses.
     */
    @SuppressWarnings("unchecked")
    private String findPlanetSign(Map<String, Object> chartData, String planetName) {
        try {
            List<Map<String, Object>> houses =
                    (List<Map<String, Object>>) chartData.get("houses");
            if (houses == null) return null;

            for (Map<String, Object> house : houses) {
                List<Map<String, Object>> planets =
                        (List<Map<String, Object>>) house.get("planets");
                if (planets == null) continue;
                for (Map<String, Object> planet : planets) {
                    if (planetName.equalsIgnoreCase((String) planet.get("name"))) {
                        return (String) planet.get("sign");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract {} sign", planetName);
        }
        return null;
    }

    /**
     * Ascendant = sign of house 1.
     */
    @SuppressWarnings("unchecked")
    private String findAscendant(Map<String, Object> chartData) {
        try {
            List<Map<String, Object>> houses =
                    (List<Map<String, Object>>) chartData.get("houses");
            if (houses == null) return null;

            for (Map<String, Object> house : houses) {
                Object houseId = house.get("house_id");
                if (houseId != null && Integer.parseInt(houseId.toString()) == 1) {
                    return (String) house.get("sign");
                }
            }
        } catch (Exception e) {
            log.warn("Could not extract ascendant");
        }
        return null;
    }
}