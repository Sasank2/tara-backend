package com.tara.mood;
import com.tara.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/mood") @RequiredArgsConstructor
public class MoodController {
    private final MoodService moodService;

    @PostMapping
    public ResponseEntity<ApiResponse<MoodDto.Response>> saveCheckin(@Valid @RequestBody MoodDto.CheckinRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Mood saved", moodService.saveCheckin(request)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<MoodDto.HistoryItem>>> getMoodHistory(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(ApiResponse.success(moodService.getMoodHistory(days)));
    }
}
