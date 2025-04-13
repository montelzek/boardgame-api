package com.montelzek.boardgameapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CollectionDTO {

    @Data
    public static class CollectionRequest {
        @NotNull(message = "Game ID is required")
        private Long gameId;
    }
}
