package com.tara.taraAi;
import com.tara.mood.MoodCheckin;
import com.tara.planets.DailyPlanets;
import com.tara.vedic.VedicChart;
import com.tara.western.WesternChart;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class TaraPromptBuilder {
    private static final String SYSTEM = """
            You are Tara, a compassionate spiritual wellness companion.
            You interpret astrological data and mood to provide practical daily guidance.
            RULES:
            - You do NOT calculate astrology. All data is pre-calculated.
            - Tone: calm, hopeful, supportive, practical. Never fear-based.
            - Use "may", "might", "could suggest" - never absolute predictions.
            RESPONSE FORMAT (valid JSON only, no markdown fences):
            {
              "shortAnswer": "1-2 sentence direct answer",
              "whyTaraSays": "2-3 sentences explaining the reasoning",
              "practicalStep": "One clear actionable next step",
              "wellnessSuggestion": "One wellness action",
              "reflectionQuestion": "One thoughtful reflection question"
            }
            """;

    public String buildSystemPrompt() { return SYSTEM; }

    public String buildUserPrompt(String question, String category, WesternChart western,
                                   VedicChart vedic, DailyPlanets planets, Optional<MoodCheckin> mood) {
        StringBuilder sb = new StringBuilder();
        sb.append("USER QUESTION: ").append(question).append("\n");
        if (category != null) sb.append("CATEGORY: ").append(category).append("\n");
        sb.append("\n=== MOOD ===\n");
        mood.ifPresentOrElse(m -> sb.append("Mood: ").append(m.getMood()).append("\n"), () -> sb.append("No mood recorded.\n"));
        if (western != null) { sb.append("\n=== WESTERN CHART ===\nSun: ").append(western.getSunSign()).append("\nMoon: ").append(western.getMoonSign()).append("\nAscendant: ").append(western.getAscendant()).append("\n"); }
        if (vedic != null) { sb.append("\n=== VEDIC CHART ===\nLagna: ").append(vedic.getLagna()).append("\nRashi: ").append(vedic.getRashi()).append("\nNakshatra: ").append(vedic.getNakshatra()).append("\n"); }
        if (planets != null) { sb.append("\n=== TODAY'S PLANETS ===\nSun in ").append(planets.getSunPosition()).append("\nMoon in ").append(planets.getMoonPosition()).append("\nMercury in ").append(planets.getMercuryPosition()).append("\n"); }
        sb.append("\nAnswer in required JSON format.");
        return sb.toString();
    }
}
