package com.tara.taraAi;

import com.tara.common.Exceptions.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "claude")
public class ClaudeAiProvider implements AiProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final int maxTokens;

    public ClaudeAiProvider(
            @Qualifier("aiWebClient") WebClient webClient,
            @Value("${ai.api-key:}") String apiKey,
            @Value("${ai.model:claude-opus-4-5}") String model,
            @Value("${ai.max-tokens:1000}") int maxTokens) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.model = model;
        this.maxTokens = maxTokens;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String complete(String systemPrompt, String userPrompt) {
        log.debug("Calling Claude model: {}", model);
        try {
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemPrompt,
                "messages", List.of(
                    Map.of("role", "user", "content", userPrompt)
                )
            );

            Map<String, Object> response = webClient.post()
                    .uri("/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null)
                throw new ExternalApiException("Claude", "Empty response");

            List<Map<String, Object>> content =
                    (List<Map<String, Object>>) response.get("content");
            return content.get(0).get("text").toString().trim();

        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Claude provider error", e);
            throw new ExternalApiException("Claude", "Request failed", e);
        }
    }
}