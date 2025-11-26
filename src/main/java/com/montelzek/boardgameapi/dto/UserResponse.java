package com.montelzek.boardgameapi.dto;

import java.util.List;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String role,
        List<GameResponse> collection,
        List<ReviewResponse> reviews
) {}
