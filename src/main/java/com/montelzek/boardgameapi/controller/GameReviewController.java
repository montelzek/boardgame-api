package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
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

    @GetMapping
    public ResponseEntity<List<ReviewDTOs.ReviewResponse>> getReviewsByGameId(@PathVariable Long gameId) {

        List<ReviewDTOs.ReviewResponse> reviews = reviewService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewDTOs.ReviewResponse> createReview(@PathVariable Long gameId,
                                                                  @Valid @RequestBody ReviewDTOs.ReviewRequest reviewRequest) {

        ReviewDTOs.ReviewResponse reviewResponse = reviewService.createReview(gameId, reviewRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/reviews/{id}")
                .buildAndExpand(reviewResponse.getId())
                .toUri();
        return ResponseEntity.created(location).body(reviewResponse);
    }
}
