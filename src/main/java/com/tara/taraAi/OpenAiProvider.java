package com.tara.taraAi;
import com.tara.common.Exceptions.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Slf4j @Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai", matchIfMissing = true)
public class OpenAiProvider implements AiProvider {
    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final int maxTokens;
    private final double temperature;

    public OpenAiProvider(@Qualifier("aiWebClient") WebClient webClient,
            @Value("${ai.api-key:}") String apiKey, @Value("${ai.model:gpt-4o}") String model,
            @Value("${ai.max-tokens:1000}") int maxTokens, @Value("${ai.temperature:0.7}") double temperature) {
        this.webClient = webClient; this.apiKey = apiKey; this.model = model;
        this.maxTokens = maxTokens; this.temperature = temperature;
    }

    @Override @SuppressWarnings("unchecked")
    public String complete(String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> body = Map.of("model", model, "max_tokens", maxTokens, "temperature", temperature,
                "messages", List.of(Map.of("role", "system", "content", systemPrompt), Map.of("role", "user", "content", userPrompt)));
            Map<String, Object> response = webClient.post().uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON).bodyValue(body)
                    .retrieve().bodyToMono(Map.class).block();
            if (response == null) throw new ExternalApiException("OpenAI", "Empty response");
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            return ((Map<String, Object>) choices.get(0).get("message")).get("content").toString().trim();
        } catch (ExternalApiException e) { throw e; }
        catch (Exception e) { throw new ExternalApiException("OpenAI", "Request failed", e); }
    }
}
