package com.montelzek.boardgameapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class GameDTOs {

    @Data
    public static class GameRequest {
        @NotBlank(message = "Title is required")
        private String title;

        private String description;

        @NotNull(message = "Minimum number of players is required")
        @Min(value = 1, message = "Minimum number of players must be at least 1")
        private Integer minPlayers;

        @NotNull(message = "Maximum number of players is required")
        @Min(value = 1, message = "Maximum number of players must be at least 1")
        private Integer maxPlayers;

        @NotNull(message = "Play time is required")
        @Min(value = 1, message = "Minimum time is 1 minute")
        private Integer playTime;

        private String publisher;

        private Integer releaseYear;
    }

    @Data
    @Builder
    public static class GameResponse {
        private Long id;
        private String title;
        private String description;
        private Integer minPlayers;
        private Integer maxPlayers;
        private Integer playTime;
        private String publisher;
        private Integer releaseYear;
        private List<ReviewDTOs.ReviewResponse> reviews;
    }
}
