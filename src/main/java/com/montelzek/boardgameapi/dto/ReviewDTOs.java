package com.montelzek.boardgameapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ReviewDTOs {

    @Data
    public static class ReviewRequest {

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Minimum rating is 1")
        @Max(value = 10, message = "Maximum rating is 10")
        private Integer rating;

        private String comment;
    }

    @Data
    @Builder
    public static class ReviewResponse {
        private Long id;
        private Long gameId;
        private String gameTitle;
        private Long userId;
        private String email;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}
