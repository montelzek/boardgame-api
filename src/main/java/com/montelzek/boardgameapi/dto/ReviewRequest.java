package com.montelzek.boardgameapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Minimum rating is 1")
        @Max(value = 10, message = "Maximum rating is 10")
        Integer rating,
        
        String comment
) {}
