package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTOs.ReviewResponse> updateReview(@PathVariable Long reviewId,
                                                                  @Valid @RequestBody ReviewDTOs.ReviewRequest reviewRequest) {

        ReviewDTOs.ReviewResponse reviewResponse = reviewService.updateReview(reviewId, reviewRequest);

        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
