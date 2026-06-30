package com.tara.vedic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.common.Exceptions.TaraException;
import com.tara.profile.BirthProfile;
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
public class VedicChartService {

    private final VedicChartRepository vedicChartRepository;
    private final AstrologyApiClient astrologyApiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public VedicChart generateChart(BirthProfile profile) {
        log.info("Generating Vedic chart for profile: {}", profile.getId());
        vedicChartRepository.findByBirthProfileId(profile.getId())
                .ifPresent(vedicChartRepository::delete);
        vedicChartRepository.flush();

        try {
            // planets returns a List
            List<Map<String, Object>> planets = astrologyApiClient.getVedicPlanets(profile);

            String lagna     = extract(planets, "Ascendant");
            String rashi     = extract(planets, "Moon");
            String nakshatra = extractNakshatra(planets);

            VedicChart chart = VedicChart.builder()
                    .birthProfile(profile)
                    .lagna(lagna)
                    .rashi(rashi)
                    .nakshatra(nakshatra)
                    .planetPositions(objectMapper.writeValueAsString(planets))
                    .chartJson(objectMapper.writeValueAsString(planets))
                    .build();

            VedicChart saved = vedicChartRepository.save(chart);
            log.info("Vedic chart saved: Lagna={}, Rashi={}, Nakshatra={}", lagna, rashi, nakshatra);
            return saved;

        } catch (Exception e) {
            log.error("Failed to generate Vedic chart", e);
            throw new TaraException("Failed to generate Vedic chart",
                    "VEDIC_CHART_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String extract(List<Map<String, Object>> planets, String planetName) {
        if (planets == null) return null;
        for (Map<String, Object> planet : planets) {
            if (planetName.equalsIgnoreCase((String) planet.get("name"))) {
                return (String) planet.get("sign");
            }
        }
        return null;
    }

    private String extractNakshatra(List<Map<String, Object>> planets) {
        if (planets == null) return null;
        for (Map<String, Object> planet : planets) {
            if ("Moon".equalsIgnoreCase((String) planet.get("name"))) {
                return (String) planet.get("nakshatra");
            }
        }
        return null;
    }
}