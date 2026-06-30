package com.tara.profile;

import com.tara.common.Exceptions.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeoNamesClient {

    private final WebClient webClient;
    private final String username;

    public GeoNamesClient(
            @Qualifier("geoNamesWebClient") WebClient webClient,
            @Value("${geonames.username}") String username) {
        this.webClient = webClient;
        this.username = username;
    }

    @SuppressWarnings("unchecked")
    public BirthProfileDto.GeoNamesResult searchLocation(String placeName) {
        log.info("GeoNames lookup: {}", placeName);
        try {
            Map<String, Object> response = webClient.get()
                    .uri(u -> u.path("/searchJSON")
                            .queryParam("q", placeName)
                            .queryParam("maxRows", 1)
                            .queryParam("username", username)
                            .build())
                    .retrieve().bodyToMono(Map.class).block();

            if (response == null) throw new ExternalApiException("GeoNames", "Empty response");

            List<Map<String, Object>> geonames = (List<Map<String, Object>>) response.get("geonames");
            if (geonames == null || geonames.isEmpty())
                throw new ExternalApiException("GeoNames", "Location not found: " + placeName);

            Map<String, Object> loc = geonames.get(0);
            BigDecimal lat = new BigDecimal(loc.get("lat").toString());
            BigDecimal lng = new BigDecimal(loc.get("lng").toString());
            String timezone = getTimezone(lat, lng);

            BirthProfileDto.GeoNamesResult result = new BirthProfileDto.GeoNamesResult();
            result.setLatitude(lat);
            result.setLongitude(lng);
            result.setTimezone(timezone);
            result.setFormattedLocation(loc.get("name") + ", " + loc.get("countryCode"));
            return result;

        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalApiException("GeoNames", "Location lookup failed: " + placeName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private String getTimezone(BigDecimal lat, BigDecimal lng) {
        try {
            Map<String, Object> tz = webClient.get()
                    .uri(u -> u.path("/timezoneJSON")
                            .queryParam("lat", lat)
                            .queryParam("lng", lng)
                            .queryParam("username", username)
                            .build())
                    .retrieve().bodyToMono(Map.class).block();
            if (tz != null && tz.get("timezoneId") != null) return tz.get("timezoneId").toString();
        } catch (Exception e) {
            log.warn("Could not resolve timezone, defaulting to UTC");
        }
        return "UTC";
    }
}
