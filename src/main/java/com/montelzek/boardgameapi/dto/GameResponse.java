package com.montelzek.boardgameapi.dto;

import java.util.List;
import java.util.Set;

public record GameResponse(
        Long id,
        String title,
        String description,
        Integer minPlayers,
        Integer maxPlayers,
        Integer playTime,
        String publisher,
        Integer releaseYear,
        Set<CategoryResponse> categories,
        List<ReviewResponse> reviews
) {}
