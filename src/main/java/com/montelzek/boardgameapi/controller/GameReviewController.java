package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.ReviewRequest;
import com.montelzek.boardgameapi.dto.ReviewResponse;
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
    public ResponseEntity<List<ReviewResponse>> getReviewsByGameId(@PathVariable Long gameId) {

        List<ReviewResponse> reviews = reviewService.getReviewsByGameId(gameId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long gameId,
                                                                  @Valid @RequestBody ReviewRequest reviewRequest) {

        ReviewResponse reviewResponse = reviewService.createReview(gameId, reviewRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/reviews/{id}")
                .buildAndExpand(reviewResponse.id())
                .toUri();
        return ResponseEntity.created(location).body(reviewResponse);
    }
}
