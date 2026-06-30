package com.tara.mood;
import com.tara.auth.AuthService;
import com.tara.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class MoodService {
    private final MoodRepository moodRepository;
    private final AuthService authService;

    @Transactional
    public MoodDto.Response saveCheckin(MoodDto.CheckinRequest request) {
        User user = authService.getCurrentUser();
        LocalDate today = LocalDate.now();
        MoodCheckin checkin = moodRepository.findByUserIdAndCheckinDate(user.getId(), today)
                .orElse(MoodCheckin.builder().user(user).checkinDate(today).build());
        checkin.setMood(request.getMood());
        checkin.setStressLevel(request.getStressLevel());
        checkin.setEnergyLevel(request.getEnergyLevel());
        checkin.setSleepQuality(request.getSleepQuality());
        checkin.setNote(request.getNote());
        return toResponse(moodRepository.save(checkin));
    }

    @Transactional(readOnly = true)
    public Optional<MoodCheckin> getLatestMood(User user) {
        return moodRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional(readOnly = true)
    public List<MoodDto.HistoryItem> getMoodHistory(int days) {
        User user = authService.getCurrentUser();
        return moodRepository.findRecentByUserId(user.getId(), LocalDate.now().minusDays(days))
                .stream().map(m -> { MoodDto.HistoryItem i = new MoodDto.HistoryItem();
                    i.setDate(m.getCheckinDate().toString()); i.setMood(m.getMood());
                    i.setStressLevel(m.getStressLevel()); i.setEnergyLevel(m.getEnergyLevel()); return i; })
                .collect(Collectors.toList());
    }

    private MoodDto.Response toResponse(MoodCheckin m) {
        MoodDto.Response r = new MoodDto.Response();
        r.setId(m.getId().toString()); r.setMood(m.getMood()); r.setStressLevel(m.getStressLevel());
        r.setEnergyLevel(m.getEnergyLevel()); r.setSleepQuality(m.getSleepQuality());
        r.setNote(m.getNote()); r.setCheckinDate(m.getCheckinDate().toString());
        return r;
    }
}
