package com.montelzek.boardgameapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class UserDTOs {

    @Data
    public static class UserUpdateRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String password;
    }

    @Data
    @Builder
    public static class UserResponse {
        private Long id;
        private String fullName;
        private String email;
        private String role;
        private List<GameDTOs.GameResponse> collection;
        private List<ReviewDTOs.ReviewResponse> reviews;
    }
}
