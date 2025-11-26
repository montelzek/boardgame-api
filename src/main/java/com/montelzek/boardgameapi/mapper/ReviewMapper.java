package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.ReviewResponse;
import com.montelzek.boardgameapi.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse mapToReviewResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getGame().getId(),
                review.getGame().getTitle(),
                review.getUser().getId(),
                review.getUser().getEmail(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
