package com.tara.journal;
import com.tara.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/journal") @RequiredArgsConstructor
public class JournalController {
    private final JournalService journalService;

    @PostMapping
    public ResponseEntity<ApiResponse<JournalDto.Response>> createEntry(@Valid @RequestBody JournalDto.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Entry saved", journalService.createEntry(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<JournalDto.PageResponse>> getEntries(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(journalService.getEntries(page, size)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalDto.Response>> updateEntry(@PathVariable String id, @RequestBody JournalDto.UpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Entry updated", journalService.updateEntry(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEntry(@PathVariable String id) {
        journalService.deleteEntry(id);
        return ResponseEntity.ok(ApiResponse.success("Entry deleted", null));
    }
}
