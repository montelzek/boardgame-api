package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/games/{gameId}/reviews")
@RequiredArgsConstructor
public class GameReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @GetMapping
    public ResponseEntity<List<ReviewDTOs.ReviewResponse>> getReviewsByGameId(@PathVariable Long gameId) {

        List<ReviewDTOs.ReviewResponse> reviews = reviewService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
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
}
