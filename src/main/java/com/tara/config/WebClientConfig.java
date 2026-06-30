package com.tara.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${geonames.base-url}")
    private String geoNamesBaseUrl;

    @Value("${astrology-api.base-url}")
    private String astrologyApiBaseUrl;

    @Value("${ai.base-url}")
    private String aiBaseUrl;

    @Bean("geoNamesWebClient")
    public WebClient geoNamesWebClient() {
        return WebClient.builder().baseUrl(geoNamesBaseUrl).build();
    }

    @Bean("astrologyApiWebClient")
    public WebClient astrologyApiWebClient() {
        return WebClient.builder().baseUrl(astrologyApiBaseUrl).build();
    }

    @Bean("aiWebClient")
    public WebClient aiWebClient() {
        return WebClient.builder().baseUrl(aiBaseUrl).build();
    }
}
