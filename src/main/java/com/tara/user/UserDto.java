package com.tara.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class UserDto {

    @Data
    public static class UpdateRequest {
        private String name;
        private String phone;
        private String profileImage;
    }

    @Data
    public static class Response {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String profileImage;
        private boolean hasBirthProfile;
        private boolean hasWesternChart;
        private boolean hasVedicChart;
        private String createdAt;
    }
}
