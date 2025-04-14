package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.model.Review;

import java.util.List;

public interface ReviewService {

    List<ReviewDTOs.ReviewResponse> getReviewsByGameId(Long gameId);

    Review createReview(Long gameId, ReviewDTOs.ReviewRequest reviewRequest);

    Review updateReview(Long reviewId, Long gameId, ReviewDTOs.ReviewRequest reviewRequest);

    void deleteReview(Long reviewId, Long gameId);
}
