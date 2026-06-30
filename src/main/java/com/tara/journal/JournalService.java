package com.tara.journal;
import com.tara.auth.AuthService;
import com.tara.common.Exceptions.ResourceNotFoundException;
import com.tara.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class JournalService {
    private final JournalRepository journalRepository;
    private final AuthService authService;

    @Transactional
    public JournalDto.Response createEntry(JournalDto.CreateRequest req) {
        User user = authService.getCurrentUser();
        return toResponse(journalRepository.save(JournalEntry.builder().user(user).prompt(req.getPrompt())
                .content(req.getContent()).mood(req.getMood()).stressLevel(req.getStressLevel()).energyLevel(req.getEnergyLevel()).build()));
    }

    @Transactional(readOnly = true)
    public JournalDto.PageResponse getEntries(int page, int size) {
        User user = authService.getCurrentUser();
        Page<JournalEntry> entries = journalRepository.findByUserIdOrderByEntryDateDesc(user.getId(), PageRequest.of(page, size));
        JournalDto.PageResponse r = new JournalDto.PageResponse();
        r.setEntries(entries.getContent().stream().map(this::toResponse).collect(Collectors.toList()));
        r.setPage(page); r.setSize(size); r.setTotalEntries(entries.getTotalElements()); r.setTotalPages(entries.getTotalPages());
        return r;
    }

    @Transactional
    public JournalDto.Response updateEntry(String id, JournalDto.UpdateRequest req) {
        User user = authService.getCurrentUser();
        JournalEntry entry = journalRepository.findByIdAndUserId(UUID.fromString(id), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry", id));
        if (req.getContent() != null) entry.setContent(req.getContent());
        if (req.getMood() != null) entry.setMood(req.getMood());
        return toResponse(journalRepository.save(entry));
    }

    @Transactional
    public void deleteEntry(String id) {
        User user = authService.getCurrentUser();
        JournalEntry entry = journalRepository.findByIdAndUserId(UUID.fromString(id), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry", id));
        journalRepository.delete(entry);
    }

    private JournalDto.Response toResponse(JournalEntry e) {
        JournalDto.Response r = new JournalDto.Response();
        r.setId(e.getId().toString()); r.setEntryDate(e.getEntryDate().toString()); r.setPrompt(e.getPrompt());
        r.setContent(e.getContent()); r.setMood(e.getMood()); r.setStressLevel(e.getStressLevel());
        r.setEnergyLevel(e.getEnergyLevel());
        r.setCreatedAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null);
        r.setUpdatedAt(e.getUpdatedAt() != null ? e.getUpdatedAt().toString() : null);
        return r;
    }
}
