package com.tara.journal;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

public class JournalDto {
    @Data public static class CreateRequest {
        private String prompt;
        @NotBlank(message = "Content is required") private String content;
        private String mood;
        private String stressLevel;
        private String energyLevel;
    }
    @Data public static class UpdateRequest { private String content; private String mood; }
    @Data public static class Response {
        private String id; private String entryDate; private String prompt;
        private String content; private String mood; private String stressLevel;
        private String energyLevel; private String createdAt; private String updatedAt;
    }
    @Data public static class PageResponse {
        private List<Response> entries; private int page; private int size;
        private long totalEntries; private int totalPages;
    }
}
