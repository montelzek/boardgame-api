package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDTOs.ReviewResponse mapToReviewResponse(Review review) {
        return ReviewDTOs.ReviewResponse.builder()
                .id(review.getId())
                .gameId(review.getGame().getId())
                .gameTitle(review.getGame().getTitle())
                .userId(review.getUser().getId())
                .email(review.getUser().getEmail())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
