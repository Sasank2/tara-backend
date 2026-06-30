package com.tara.home;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tara.auth.AuthService;
import com.tara.mood.MoodCheckin;
import com.tara.mood.MoodRepository;
import com.tara.planets.DailyPlanets;
import com.tara.planets.PlanetsService;
import com.tara.taraAi.AiProvider;
import com.tara.user.User;
import com.tara.vedic.VedicChart;
import com.tara.vedic.VedicChartRepository;
import com.tara.western.WesternChart;
import com.tara.western.WesternChartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j @Service @RequiredArgsConstructor
public class HomeService {
    private final AiProvider aiProvider;
    private final DailyGuidanceRepository guidanceRepository;
    private final WesternChartRepository westernChartRepository;
    private final VedicChartRepository vedicChartRepository;
    private final MoodRepository moodRepository;
    private final PlanetsService planetsService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            You are Tara, generating a user's daily guidance card.
            RULES: Do NOT calculate astrology. Only interpret. Tone: calm, hopeful, practical.
            RESPONSE FORMAT (valid JSON only):
            {"energy":"High|Medium|Low","focusArea":"e.g. Career","whatTodayMeans":"2-3 sentences","focusGuidance":"1-2 sentences","avoidGuidance":"1-2 sentences","favorableTime":"e.g. 10:00 AM - 11:30 AM","wellnessAction":"one action","practicalStep":"one step","reflectionPrompt":"one question"}
            """;

    @Transactional
    public HomeDto.HomeResponse getHome() {
        User user = authService.getCurrentUser();
        LocalDate today = LocalDate.now();
        var western = westernChartRepository.findPrimaryByUserId(user.getId()).orElse(null);
        var vedic = vedicChartRepository.findPrimaryByUserId(user.getId()).orElse(null);
        var planets = planetsService.getTodayPlanets();
        var latestMood = moodRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
        var todayMood = moodRepository.findByUserIdAndCheckinDate(user.getId(), today);
        DailyGuidance guidance = guidanceRepository.findByUserIdAndGuidanceDate(user.getId(), today)
                .orElseGet(() -> generateGuidance(user, western, vedic, planets, latestMood));
        return buildResponse(user, guidance, western, vedic, planets, todayMood);
    }

    @Transactional
    public HomeDto.HomeResponse refreshGuidance() {
        User user = authService.getCurrentUser();
        guidanceRepository.findByUserIdAndGuidanceDate(user.getId(), LocalDate.now()).ifPresent(guidanceRepository::delete);
        return getHome();
    }

    private DailyGuidance generateGuidance(User user, WesternChart w, VedicChart v, DailyPlanets p, Optional<MoodCheckin> mood) {
        log.info("Generating daily guidance for user: {}", user.getId());
        String userPrompt = buildUserPrompt(w, v, p, mood);
        String aiResponse = aiProvider.complete(SYSTEM_PROMPT, userPrompt);
        return parseAndSave(aiResponse, user, w, v, p);
    }

    @SuppressWarnings("unchecked")
    private DailyGuidance parseAndSave(String aiResponse, User user, WesternChart w, VedicChart v, DailyPlanets p) {
        String energy = "Medium", focusArea = null, whatTodayMeans = null, focusGuidance = null,
               avoidGuidance = null, favorableTime = null, wellnessAction = null, practicalStep = null, reflectionPrompt = null;
        try {
            String clean = aiResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            Map<String, Object> parsed = objectMapper.readValue(clean, Map.class);
            energy = str(parsed, "energy", "Medium"); focusArea = str(parsed, "focusArea", null);
            whatTodayMeans = str(parsed, "whatTodayMeans", null); focusGuidance = str(parsed, "focusGuidance", null);
            avoidGuidance = str(parsed, "avoidGuidance", null); favorableTime = str(parsed, "favorableTime", null);
            wellnessAction = str(parsed, "wellnessAction", null); practicalStep = str(parsed, "practicalStep", null);
            reflectionPrompt = str(parsed, "reflectionPrompt", null);
        } catch (Exception e) { log.warn("Failed to parse guidance JSON", e); whatTodayMeans = aiResponse; }
        return guidanceRepository.save(DailyGuidance.builder().user(user).guidanceDate(LocalDate.now())
                .energy(energy).focusArea(focusArea).whatTodayMeans(whatTodayMeans).focusGuidance(focusGuidance)
                .avoidGuidance(avoidGuidance).favorableTime(favorableTime).wellnessAction(wellnessAction)
                .practicalStep(practicalStep).reflectionPrompt(reflectionPrompt).build());
    }

    private HomeDto.HomeResponse buildResponse(User user, DailyGuidance g, WesternChart w, VedicChart v, DailyPlanets p, Optional<MoodCheckin> todayMood) {
        HomeDto.HomeResponse r = new HomeDto.HomeResponse();
        HomeDto.UserGreeting greeting = new HomeDto.UserGreeting();
        greeting.setName(user.getName()); greeting.setGreeting(greetingText());
        greeting.setDate(formatDate(LocalDate.now())); greeting.setHasMoodToday(todayMood.isPresent());
        r.setGreeting(greeting);
        HomeDto.AstrologyHeader header = new HomeDto.AstrologyHeader();
        if (p != null) { header.setSunSign(p.getSunPosition()); header.setMoonSign(p.getMoonPosition()); }
        if (w != null) { header.setSunSignNatal(w.getSunSign()); header.setMoonSignNatal(w.getMoonSign()); header.setAscendant(w.getAscendant()); }
        r.setAstrologyHeader(header);
        HomeDto.TodaysGuidance tg = new HomeDto.TodaysGuidance();
        tg.setId(g.getId().toString()); tg.setEnergy(g.getEnergy()); tg.setFocusArea(g.getFocusArea());
        tg.setWhatTodayMeans(g.getWhatTodayMeans()); tg.setFocusGuidance(g.getFocusGuidance());
        tg.setAvoidGuidance(g.getAvoidGuidance()); tg.setFavorableTime(g.getFavorableTime());
        tg.setWellnessAction(g.getWellnessAction()); tg.setPracticalStep(g.getPracticalStep());
        tg.setReflectionPrompt(g.getReflectionPrompt()); tg.setGeneratedAt(g.getCreatedAt() != null ? g.getCreatedAt().toString() : null);
        r.setTodaysGuidance(tg);
        HomeDto.MoodSummary ms = new HomeDto.MoodSummary();
        todayMood.ifPresentOrElse(m -> { ms.setMood(m.getMood()); ms.setStressLevel(m.getStressLevel()); ms.setEnergyLevel(m.getEnergyLevel()); ms.setCheckedInToday(true); }, () -> ms.setCheckedInToday(false));
        r.setCurrentMood(ms);
        r.setReflectionPrompt(g.getReflectionPrompt());
        return r;
    }

    private String buildUserPrompt(WesternChart w, VedicChart v, DailyPlanets p, Optional<MoodCheckin> mood) {
        StringBuilder sb = new StringBuilder("Generate today's daily guidance card.\n\n=== MOOD ===\n");
        mood.ifPresentOrElse(m -> sb.append("Mood: ").append(m.getMood()).append("\n"), () -> sb.append("No mood today.\n"));
        if (w != null) sb.append("\n=== WESTERN CHART ===\nSun: ").append(w.getSunSign()).append("\nMoon: ").append(w.getMoonSign()).append("\nAsc: ").append(w.getAscendant()).append("\n");
        if (v != null) sb.append("\n=== VEDIC CHART ===\nLagna: ").append(v.getLagna()).append("\nRashi: ").append(v.getRashi()).append("\nNakshatra: ").append(v.getNakshatra()).append("\n");
        if (p != null) sb.append("\n=== TODAY'S PLANETS ===\nSun in ").append(p.getSunPosition()).append("\nMoon in ").append(p.getMoonPosition()).append("\n");
        sb.append("\nGenerate the guidance JSON now.");
        return sb.toString();
    }

    private String greetingText() {
        int h = LocalTime.now(ZoneId.of("UTC")).getHour();
        if (h < 12) return "Good morning"; if (h < 17) return "Good afternoon"; return "Good evening";
    }

    private String formatDate(LocalDate d) {
        return d.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getDayOfMonth();
    }

    private String str(Map<String, Object> m, String k, String def) { Object v = m.get(k); return v != null ? v.toString() : def; }
}
