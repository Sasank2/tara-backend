package com.tara.taraAi;
import com.tara.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/taraAi") @RequiredArgsConstructor
public class TaraAiController {
    private final TaraAiService taraAiService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<TaraDto.ChatResponse>> chat(@Valid @RequestBody TaraDto.ChatRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taraAiService.chat(request)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TaraDto.ConversationSummary>>> getHistory(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(taraAiService.getHistory(limit)));
    }

    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<List<TaraDto.ConversationSummary>>> getSaved() {
        return ResponseEntity.ok(ApiResponse.success(taraAiService.getSaved()));
    }

    @PatchMapping("/{id}/save")
    public ResponseEntity<ApiResponse<TaraDto.ChatResponse>> toggleSave(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(taraAiService.toggleSave(id)));
    }
}
