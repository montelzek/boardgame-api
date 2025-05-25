package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.model.Role;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.GameRepository;
import com.montelzek.boardgameapi.repository.ReviewRepository;
import com.montelzek.boardgameapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review1;
    private Game game;
    private User user;
    private ReviewDTOs.ReviewResponse reviewResponse;
    private ReviewDTOs.ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .games(new HashSet<>())
                .reviews(new HashSet<>())
                .build();

        game = Game.builder()
                .id(1L)
                .title("Test Game")
                .description("Description")
                .minPlayers(2)
                .maxPlayers(8)
                .playTime(180)
                .publisher("Publisher")
                .releaseYear(2015)
                .build();

        review1 = Review.builder()
                .id(1L)
                .game(game)
                .user(user)
                .rating(7)
                .comment("Test Comment")
                .createdAt(LocalDateTime.now())
                .build();

        reviewResponse = ReviewDTOs.ReviewResponse.builder()
                .id(1L)
                .gameId(game.getId())
                .gameTitle(game.getTitle())
                .userId(user.getId())
                .email(user.getEmail())
                .rating(7)
                .comment("Test Comment")
                .createdAt(review1.getCreatedAt())
                .build();

        reviewRequest = ReviewDTOs.ReviewRequest.builder()
                .rating(9)
                .comment("Excellent game!")
                .build();
    }

    @Test
    void getReviewsByGameIdTest_whenGameExistsAndHaveOneReviews_shouldReturnListOfReviewResponses() {
        // Arrange
        List<Review> reviews = List.of(review1);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGame(game)).thenReturn(reviews);
        when(reviewMapper.mapToReviewResponse(any(Review.class))).thenReturn(reviewResponse);

        // Act
        List<ReviewDTOs.ReviewResponse> actualResponse = reviewService.getReviewsByGameId(game.getId());

        // Assert
        assertNotNull(actualResponse);
        assertEquals(actualResponse.size(), reviews.size());
        ReviewDTOs.ReviewResponse firstReview = actualResponse.getFirst();
        assertEquals(review1.getId(), firstReview.getId());
        assertEquals(review1.getGame().getId(), firstReview.getGameId());
        assertEquals(review1.getGame().getTitle(), firstReview.getGameTitle());
        assertEquals(review1.getUser().getId(), firstReview.getUserId());
        assertEquals(review1.getUser().getEmail(), firstReview.getEmail());
        assertEquals(review1.getRating(), firstReview.getRating());
        assertEquals(review1.getComment(), firstReview.getComment());
        assertEquals(review1.getCreatedAt(), firstReview.getCreatedAt());

        verify(gameRepository).findById(1L);
        verify(reviewRepository).findByGame(game);
        verify(reviewMapper).mapToReviewResponse(review1);
    }

    @Test
    void getReviewsByGameIdTest_whenGameExistsAndHaveZeroReviews_shouldReturnEmptyListOfReviewResponses() {
        // Arrange
        List<Review> reviews = Collections.emptyList();
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGame(game)).thenReturn(reviews);

        // Act
        List<ReviewDTOs.ReviewResponse> actualResponse = reviewService.getReviewsByGameId(game.getId());

        // Assert
        assertNotNull(actualResponse);
        assertEquals(0, actualResponse.size());

        verify(gameRepository).findById(1L);
        verify(reviewRepository).findByGame(game);
    }

    @Test
    void getReviewsByGameIdTest_whenGameNotExists_shouldThrowResourceNOtFoundException() {
        // Arrange
        Long nonExistentGameId = -1L;
        when(gameRepository.findById(nonExistentGameId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                reviewService.getReviewsByGameId(nonExistentGameId));
        assertEquals("Game not found with id: " + nonExistentGameId, exception.getMessage());

        verify(gameRepository).findById(nonExistentGameId);
    }

    @Test
    void createReview_whenValidRequestAndUserAndGameExist_shouldCreateAndReturnReviewResponse() {
        // Arrange
        Long currentUserId = user.getId();
        Long gameId = game.getId();
        LocalDateTime fixedTestTime = LocalDateTime.now();

        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGameIdAndUserId(gameId, currentUserId)).thenReturn(Optional.empty());

        Review savedReview = Review.builder()
                .id(2L)
                .user(user)
                .game(game)
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .createdAt(fixedTestTime)
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewDTOs.ReviewResponse expectedResponse = ReviewDTOs.ReviewResponse.builder()
                .id(savedReview.getId())
                .userId(savedReview.getUser().getId())
                .email(savedReview.getUser().getEmail())
                .gameId(savedReview.getGame().getId())
                .gameTitle(savedReview.getGame().getTitle())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .build();

        when(reviewMapper.mapToReviewResponse(savedReview)).thenReturn(expectedResponse);

        // Act
        ReviewDTOs.ReviewResponse actualResponse = reviewService.createReview(gameId, reviewRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getUserId(), actualResponse.getUserId());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedResponse.getGameId(), actualResponse.getGameId());
        assertEquals(expectedResponse.getGameTitle(), actualResponse.getGameTitle());
        assertEquals(expectedResponse.getRating(), actualResponse.getRating());
        assertEquals(expectedResponse.getComment(), actualResponse.getComment());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());

        verify(userService, times(2)).getCurrentUserId();
        verify(userRepository).findById(currentUserId);
        verify(gameRepository).findById(gameId);
        verify(reviewRepository).findByGameIdAndUserId(gameId, currentUserId);

        ArgumentCaptor<Review> reviewArgumentCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgumentCaptor.capture());
        Review capturedReviewForSave = reviewArgumentCaptor.getValue();

        assertNull(capturedReviewForSave.getId());
        assertEquals(user, capturedReviewForSave.getUser());
        assertEquals(game, capturedReviewForSave.getGame());
        assertEquals(reviewRequest.getRating(), capturedReviewForSave.getRating());
        assertEquals(reviewRequest.getComment(), capturedReviewForSave.getComment());
        assertEquals(game, capturedReviewForSave.getGame());

        verify(reviewMapper).mapToReviewResponse(savedReview);
    }
}
