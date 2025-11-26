package com.montelzek.boardgameapi.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long gameId,
        String gameTitle,
        Long userId,
        String email,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
