package com.tara.taraAi;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class TaraDto {
    @Data public static class ChatRequest {
        @NotBlank(message = "Question is required") private String question;
        private String category;
    }
    @Data public static class ChatResponse {
        private String id;
        private String category;
        private String question;
        private String shortAnswer;
        private String whyTaraSays;
        private String practicalStep;
        private String wellnessSuggestion;
        private String reflectionQuestion;
        private String disclaimer;
        private boolean isSaved;
        private String createdAt;
    }
    @Data public static class ConversationSummary {
        private String id;
        private String category;
        private String question;
        private String shortAnswer;
        private boolean isSaved;
        private String createdAt;
    }
}
