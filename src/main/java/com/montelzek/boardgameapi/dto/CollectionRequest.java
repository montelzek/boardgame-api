package com.montelzek.boardgameapi.dto;

import jakarta.validation.constraints.NotNull;

public record CollectionRequest(
        @NotNull(message = "Game ID is required")
        Long gameId
) {}
