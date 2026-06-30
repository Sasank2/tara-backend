package com.tara.vedic;

import com.tara.common.Exceptions.ExternalApiException;
import com.tara.profile.BirthProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.List;

@Slf4j
@Component
public class AstrologyApiClient {

    private final WebClient webClient;
    private final String userId;
    private final String apiKey;

    public AstrologyApiClient(
            @Qualifier("astrologyApiWebClient") WebClient webClient,
            @Value("${astrology-api.user-id}") String userId,
            @Value("${astrology-api.api-key}") String apiKey) {
        this.webClient = webClient;
        this.userId = userId;
        this.apiKey = apiKey;
    }

    // Vedic endpoints
    public Map<String, Object> getBirthChart(BirthProfile p) { return post("/birth_details", birthRequest(p)); }
    public List<Map<String, Object>> getVedicPlanets(BirthProfile p) { return postList("/planets", birthRequest(p)); }
    public Map<String, Object> getCurrentDasha(BirthProfile p) { return post("/current_vdasha", birthRequest(p)); }

    // Western endpoints
    public Map<String, Object> getWesternPlanets(BirthProfile p) { return post("/western_chart_data", birthRequest(p)); }
    public Map<String, Object> getWesternHouses(BirthProfile p) { return post("/western_chart_data", birthRequest(p)); }

    // Daily planetary positions
    public List<Map<String, Object>> getDailyPlanetaryPositions(LocalDate date) {
    return postList("/planets/tropical", Map.of(

            "day", date.getDayOfMonth(), "month", date.getMonthValue(), "year", date.getYear(),
            "hour", 0, "min", 0, "lat", 0.0, "lon", 0.0, "tzone", 0.0
        ));
    }

    private Map<String, Object> birthRequest(BirthProfile p) {
        ZonedDateTime dt = p.getDateOfBirth().atTime(p.getTimeOfBirth()).atZone(ZoneId.of(p.getTimezone()));
        return Map.of(
            "day", dt.getDayOfMonth(), "month", dt.getMonthValue(), "year", dt.getYear(),
            "hour", dt.getHour(), "min", dt.getMinute(),
            "lat", p.getLatitude().doubleValue(), "lon", p.getLongitude().doubleValue(),
            "tzone", tzOffset(p.getTimezone())
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String path, Map<String, Object> body) {
        try {
            Map<String, Object> response = webClient.post()
                    .uri(path)
                    .header(HttpHeaders.AUTHORIZATION, basicAuth())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response == null) throw new ExternalApiException("AstrologyAPI", "Empty response from " + path);
            return response;
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("AstrologyAPI error [{}]: {}", path, e.getMessage());
            throw new ExternalApiException("AstrologyAPI", "Request failed: " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> postList(String path, Map<String, Object> body) {
        try {
            List<Map<String, Object>> response = webClient.post()
                .uri(path)
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(List.class)
                .block();
            if (response == null) throw new ExternalApiException("AstrologyAPI", "Empty response from " + path);
            return response;
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("AstrologyAPI error [{}]: {}", path, e.getMessage());
         throw new ExternalApiException("AstrologyAPI", "Request failed: " + path, e);
        }
    }

    private String basicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString((userId + ":" + apiKey).getBytes());
    }

    private double tzOffset(String timezoneId) {
        try {
            return ZoneId.of(timezoneId).getRules().getOffset(java.time.Instant.now()).getTotalSeconds() / 3600.0;
        } catch (Exception e) { return 5.5; }
    }
}
