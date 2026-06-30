package com.tara.mood;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

public class MoodDto {
    @Data
    public static class CheckinRequest {
        @NotBlank(message = "Mood is required")
        private String mood;
        private String stressLevel;
        private String energyLevel;
        private String sleepQuality;
        private String note;
    }
    @Data
    public static class Response {
        private String id;
        private String mood;
        private String stressLevel;
        private String energyLevel;
        private String sleepQuality;
        private String note;
        private String checkinDate;
    }
    @Data
    public static class HistoryItem {
        private String date;
        private String mood;
        private String stressLevel;
        private String energyLevel;
    }
}
