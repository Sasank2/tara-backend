package com.tara.home;
import lombok.Data;

public class HomeDto {
    @Data public static class HomeResponse {
        private UserGreeting greeting;
        private AstrologyHeader astrologyHeader;
        private TodaysGuidance todaysGuidance;
        private MoodSummary currentMood;
        private String reflectionPrompt;
    }
    @Data public static class UserGreeting {
        private String name;
        private String greeting;
        private String date;
        private boolean hasMoodToday;
    }
    @Data public static class AstrologyHeader {
        private String sunSign;
        private String moonSign;
        private String sunSignNatal;
        private String moonSignNatal;
        private String ascendant;
    }
    @Data public static class TodaysGuidance {
        private String id;
        private String energy;
        private String focusArea;
        private String whatTodayMeans;
        private String focusGuidance;
        private String avoidGuidance;
        private String favorableTime;
        private String wellnessAction;
        private String practicalStep;
        private String reflectionPrompt;
        private String generatedAt;
    }
    @Data public static class MoodSummary {
        private String mood;
        private String stressLevel;
        private String energyLevel;
        private boolean checkedInToday;
    }
}
