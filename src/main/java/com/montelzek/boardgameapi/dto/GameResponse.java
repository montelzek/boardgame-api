package com.montelzek.boardgameapi.dto;

import com.montelzek.boardgameapi.dto.CategoryDto;
import com.montelzek.boardgameapi.dto.ReviewResponse;

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
        Set<CategoryDto> categories,
        List<ReviewResponse> reviews
) {}
