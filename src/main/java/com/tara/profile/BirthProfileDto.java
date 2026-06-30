package com.tara.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

public class BirthProfileDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Date of birth is required (YYYY-MM-DD)")
        private String dateOfBirth;

        @NotBlank(message = "Time of birth is required (HH:mm)")
        private String timeOfBirth;

        @NotBlank(message = "Place of birth is required")
        private String placeOfBirth;

        private BigDecimal latitude;
        private BigDecimal longitude;
        private String timezone;
        private String profileName;
    }


    @Data
    public static class UpdateRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Date of birth is required (YYYY-MM-DD)")
        private String dateOfBirth;

        @NotBlank(message = "Time of birth is required (HH:mm)")
        private String timeOfBirth;

        @NotBlank(message = "Place of birth is required")
        private String placeOfBirth;

        private BigDecimal latitude;
        private BigDecimal longitude;
        private String timezone;
        private String profileName;
    }

    @Data
    public static class Response {
        private String id;
        private String profileName;
        private String fullName;
        private String dateOfBirth;
        private String timeOfBirth;
        private String placeOfBirth;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String timezone;
        private boolean isPrimary;
    }

    @Data
    public static class GeoNamesResult {
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String timezone;
        private String formattedLocation;
    }
}
