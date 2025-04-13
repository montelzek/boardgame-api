package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @GetMapping("/games/{gameId}/reviews")
    public ResponseEntity<List<ReviewDTOs.ReviewResponse>> getReviewsByGameId(@PathVariable Long gameId) {

        List<ReviewDTOs.ReviewResponse> reviews = reviewService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/games/{gameId}/reviews")
    public ResponseEntity<ReviewDTOs.ReviewResponse> createReview(@PathVariable Long gameId,
                                                                  @Valid @RequestBody ReviewDTOs.ReviewRequest reviewRequest) {
        Review review = reviewService.createReview(gameId, reviewRequest);

        ReviewDTOs.ReviewResponse reviewResponse = reviewMapper.mapToReviewResponse(review);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/reviews/{id}")
                .buildAndExpand(review.getId())
                .toUri();
        return ResponseEntity.created(location).body(reviewResponse);
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDTOs.ReviewResponse> updateReview(@PathVariable Long reviewId,
                                                                  @Valid @RequestBody ReviewDTOs.ReviewRequest reviewRequest) {
        Review review = reviewService.updateReview(reviewId, reviewRequest);

        ReviewDTOs.ReviewResponse reviewResponse = reviewMapper.mapToReviewResponse(review);

        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>("Review of id " + reviewId + " was successfully removed!", HttpStatus.NO_CONTENT);
    }
}
