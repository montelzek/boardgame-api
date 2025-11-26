package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.ReviewRequest;
import com.montelzek.boardgameapi.dto.ReviewResponse;
import com.montelzek.boardgameapi.model.Review;

import java.util.List;

public interface ReviewService {

    List<ReviewResponse> getReviewsByGameId(Long gameId);

    ReviewResponse createReview(Long gameId, ReviewRequest reviewRequest);

    ReviewResponse updateReview(Long reviewId, ReviewRequest reviewRequest);

    void deleteReview(Long reviewId);
}
