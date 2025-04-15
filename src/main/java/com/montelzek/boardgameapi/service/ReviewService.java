package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.model.Review;

import java.util.List;

public interface ReviewService {

    List<ReviewDTOs.ReviewResponse> getReviewsByGameId(Long gameId);

    ReviewDTOs.ReviewResponse createReview(Long gameId, ReviewDTOs.ReviewRequest reviewRequest);

    ReviewDTOs.ReviewResponse updateReview(Long reviewId, ReviewDTOs.ReviewRequest reviewRequest);

    void deleteReview(Long reviewId);
}
