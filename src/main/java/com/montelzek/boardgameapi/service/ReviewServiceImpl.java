package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.GameRepository;
import com.montelzek.boardgameapi.repository.ReviewRepository;
import com.montelzek.boardgameapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ReviewMapper reviewMapper;
    private final UserServiceImpl userService;

    @Override
    public List<ReviewDTOs.ReviewResponse> getReviewsByGameId(Long gameId) {
        List<Review> reviews = reviewRepository.findByGameId(gameId);

        return reviews.stream()
                .map(reviewMapper::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Review createReview(Long gameId, ReviewDTOs.ReviewRequest reviewRequest) {

        User user = userRepository.findById(userService.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        if (reviewRepository.findByGameIdAndUserId(gameId, userService.getCurrentUserId()).isPresent()) {
            throw new IllegalStateException("You have already reviewed this game");
        }


        Review review = new Review();
        review.setGame(game);
        review.setUser(user);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long reviewId, Long gameId, ReviewDTOs.ReviewRequest reviewRequest) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getGame().getId().equals(gameId)) {
            throw new ResourceNotFoundException("Review with id " + reviewId + " does not belong to game with id " + gameId);
        }

        if (!userService.getCurrentUserId().equals(review.getUser().getId())) {
            throw new AccessDeniedException("You are not authorized to update this review");
        }

        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long gameId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (!review.getGame().getId().equals(gameId)) {
            throw new ResourceNotFoundException("Review with id " + reviewId + " does not belong to game with id " + gameId);
        }

        if (!userService.getCurrentUserId().equals(review.getUser().getId()) &&
                !SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You are not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }
}
