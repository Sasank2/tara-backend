package com.tara.taraAi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.auth.AuthService;
import com.tara.common.Exceptions.ResourceNotFoundException;
import com.tara.mood.MoodRepository;
import com.tara.planets.PlanetsService;
import com.tara.user.User;
import com.tara.vedic.VedicChartRepository;
import com.tara.western.WesternChartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class TaraAiService {
    private final AiProvider aiProvider;
    private final TaraPromptBuilder promptBuilder;
    private final TaraConversationRepository conversationRepository;
    private final WesternChartRepository westernChartRepository;
    private final VedicChartRepository vedicChartRepository;
    private final MoodRepository moodRepository;
    private final PlanetsService planetsService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private static final String DISCLAIMER = "This guidance is for personal reflection only. It does not replace professional advice.";

    @Transactional
    public TaraDto.ChatResponse chat(TaraDto.ChatRequest request) {
        User user = authService.getCurrentUser();
        var western = westernChartRepository.findPrimaryByUserId(user.getId()).orElse(null);
        var vedic = vedicChartRepository.findPrimaryByUserId(user.getId()).orElse(null);
        var mood = moodRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
        var planets = planetsService.getTodayPlanets();
        String aiResponse = aiProvider.complete(promptBuilder.buildSystemPrompt(),
                promptBuilder.buildUserPrompt(request.getQuestion(), request.getCategory(), western, vedic, planets, mood));
        return toDto(parseAndSave(aiResponse, request, user));
    }

    @Transactional(readOnly = true)
    public List<TaraDto.ConversationSummary> getHistory(int limit) {
        User user = authService.getCurrentUser();
        return conversationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, limit))
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaraDto.ConversationSummary> getSaved() {
        return conversationRepository.findSavedByUserId(authService.getCurrentUser().getId())
                .stream().map(this::toSummary).collect(Collectors.toList());
    }

    @Transactional
    public TaraDto.ChatResponse toggleSave(String id) {
        User user = authService.getCurrentUser();
        TaraConversation conv = conversationRepository.findByIdAndUserId(UUID.fromString(id), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", id));
        conv.setSaved(!conv.isSaved());
        return toDto(conversationRepository.save(conv));
    }

    @SuppressWarnings("unchecked")
    private TaraConversation parseAndSave(String aiResponse, TaraDto.ChatRequest request, User user) {
        String shortAnswer = null, whyTaraSays = null, practicalStep = null, wellnessSuggestion = null, reflectionQuestion = null;
        try {
            String clean = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            Map<String, Object> parsed = objectMapper.readValue(clean, Map.class);
            shortAnswer = (String) parsed.get("shortAnswer"); whyTaraSays = (String) parsed.get("whyTaraSays");
            practicalStep = (String) parsed.get("practicalStep"); wellnessSuggestion = (String) parsed.get("wellnessSuggestion");
            reflectionQuestion = (String) parsed.get("reflectionQuestion");
        } catch (Exception e) { log.warn("Failed to parse AI JSON", e); shortAnswer = aiResponse; }
        return conversationRepository.save(TaraConversation.builder().user(user).category(request.getCategory())
                .question(request.getQuestion()).shortAnswer(shortAnswer).whyTaraSays(whyTaraSays)
                .practicalStep(practicalStep).wellnessSuggestion(wellnessSuggestion).reflectionQuestion(reflectionQuestion).build());
    }

    private TaraDto.ChatResponse toDto(TaraConversation c) {
        TaraDto.ChatResponse d = new TaraDto.ChatResponse();
        d.setId(c.getId().toString()); d.setCategory(c.getCategory()); d.setQuestion(c.getQuestion());
        d.setShortAnswer(c.getShortAnswer()); d.setWhyTaraSays(c.getWhyTaraSays());
        d.setPracticalStep(c.getPracticalStep()); d.setWellnessSuggestion(c.getWellnessSuggestion());
        d.setReflectionQuestion(c.getReflectionQuestion()); d.setDisclaimer(DISCLAIMER);
        d.setSaved(c.isSaved()); d.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        return d;
    }

    private TaraDto.ConversationSummary toSummary(TaraConversation c) {
        TaraDto.ConversationSummary s = new TaraDto.ConversationSummary();
        s.setId(c.getId().toString()); s.setCategory(c.getCategory()); s.setQuestion(c.getQuestion());
        s.setShortAnswer(c.getShortAnswer()); s.setSaved(c.isSaved()); s.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        return s;
    }
}
