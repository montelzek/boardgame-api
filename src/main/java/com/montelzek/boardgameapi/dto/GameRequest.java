package com.montelzek.boardgameapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GameRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title can't be longer than 255 characters")
        String title,

        String description,

        @NotNull(message = "Minimum number of players is required")
        @Min(value = 1, message = "Minimum number of players must be at least 1")
        Integer minPlayers,

        @NotNull(message = "Maximum number of players is required")
        @Min(value = 1, message = "Maximum number of players must be at least 1")
        Integer maxPlayers,

        @NotNull(message = "Play time is required")
        @Min(value = 1, message = "Minimum time is 1 minute")
        Integer playTime,

        String publisher,

        Integer releaseYear
) {}
