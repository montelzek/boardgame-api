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

    private Review review;
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

        review = Review.builder()
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
                .createdAt(review.getCreatedAt())
                .build();

        reviewRequest = ReviewDTOs.ReviewRequest.builder()
                .rating(9)
                .comment("Excellent game!")
                .build();
    }

    @Test
    void getReviewsByGameIdTest_whenGameExistsAndHaveOneReviews_shouldReturnListOfReviewResponses() {
        // Arrange
        List<Review> reviews = List.of(review);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGame(game)).thenReturn(reviews);
        when(reviewMapper.mapToReviewResponse(any(Review.class))).thenReturn(reviewResponse);

        // Act
        List<ReviewDTOs.ReviewResponse> actualResponse = reviewService.getReviewsByGameId(game.getId());

        // Assert
        assertNotNull(actualResponse);
        assertEquals(actualResponse.size(), reviews.size());
        ReviewDTOs.ReviewResponse firstReview = actualResponse.getFirst();
        assertEquals(review.getId(), firstReview.getId());
        assertEquals(review.getGame().getId(), firstReview.getGameId());
        assertEquals(review.getGame().getTitle(), firstReview.getGameTitle());
        assertEquals(review.getUser().getId(), firstReview.getUserId());
        assertEquals(review.getUser().getEmail(), firstReview.getEmail());
        assertEquals(review.getRating(), firstReview.getRating());
        assertEquals(review.getComment(), firstReview.getComment());
        assertEquals(review.getCreatedAt(), firstReview.getCreatedAt());

        verify(gameRepository).findById(1L);
        verify(reviewRepository).findByGame(game);
        verify(reviewMapper).mapToReviewResponse(review);
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

    @Test
    void createReviewTest_whenUserNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long currentUserId = 1L;
        Long gameId = game.getId();
        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                reviewService.createReview(gameId, reviewRequest));

        assertEquals("User not found", exception.getMessage());
        verify(userService).getCurrentUserId();
        verify(userRepository).findById(currentUserId);
        verify(gameRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReviewTest_whenGameNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long currentUserId = user.getId();
        Long nonExistentGameId = -1L;
        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(nonExistentGameId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                reviewService.createReview(nonExistentGameId, reviewRequest));

        assertEquals("Game not found with id: " + nonExistentGameId, exception.getMessage());
        verify(userService).getCurrentUserId();
        verify(userRepository).findById(currentUserId);
        verify(gameRepository).findById(nonExistentGameId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReviewTest_whenUserAlreadyReviewedGame_shouldThrowIllegalStateException() {
        // Arrange
        Long currentUserId = user.getId();
        Long gameId = game.getId();
        Review existingReview = Review.builder().id(3L).user(user).game(game).build();

        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(user));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGameIdAndUserId(gameId, currentUserId)).thenReturn(Optional.of(existingReview));


        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reviewService.createReview(gameId, reviewRequest);
        });

        assertEquals("You have already reviewed this game", exception.getMessage());
        verify(userService, times(2)).getCurrentUserId();
        verify(userRepository).findById(currentUserId);
        verify(gameRepository).findById(gameId);
        verify(reviewRepository).findByGameIdAndUserId(gameId, currentUserId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void updateReviewTest_whenReviewExistsAndUserIsOwnerOfReview_shouldUpdateReviewAndReturnReviewResponse() {
        // Arrange
        Long reviewIdToUpdate = review.getId();
        Long currentUserId = user.getId();

        ReviewDTOs.ReviewRequest updateReviewRequest = ReviewDTOs.ReviewRequest.builder()
                .rating(5)
                .comment("Updated comment")
                .build();

        when(reviewRepository.findById(reviewIdToUpdate)).thenReturn(Optional.of(review));
        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        ReviewDTOs.ReviewResponse expectedReviewResponse = ReviewDTOs.ReviewResponse.builder()
                .id(review.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .gameId(game.getId())
                .gameTitle(game.getTitle())
                .rating(updateReviewRequest.getRating())
                .comment(updateReviewRequest.getComment())
                .createdAt(review.getCreatedAt())
                .build();
        when(reviewMapper.mapToReviewResponse(any(Review.class))).thenReturn(expectedReviewResponse);

        // Act
        ReviewDTOs.ReviewResponse actualResponse = reviewService.updateReview(reviewIdToUpdate, updateReviewRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedReviewResponse.getId(), actualResponse.getId());
        assertEquals(expectedReviewResponse.getUserId(), actualResponse.getUserId());
        assertEquals(expectedReviewResponse.getEmail(), actualResponse.getEmail());
        assertEquals(expectedReviewResponse.getGameId(), actualResponse.getGameId());
        assertEquals(expectedReviewResponse.getGameTitle(), actualResponse.getGameTitle());
        assertEquals(updateReviewRequest.getRating(), actualResponse.getRating());
        assertEquals(updateReviewRequest.getComment(), actualResponse.getComment());
        assertEquals(expectedReviewResponse.getCreatedAt(), actualResponse.getCreatedAt());

        verify(reviewRepository).findById(reviewIdToUpdate);
        verify(userService).getCurrentUserId();

        ArgumentCaptor<Review> reviewArgumentCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgumentCaptor.capture());
        Review capturedReviewForSave = reviewArgumentCaptor.getValue();

        assertEquals(review.getId(), capturedReviewForSave.getId());
        assertEquals(user, capturedReviewForSave.getUser());
        assertEquals(game, capturedReviewForSave.getGame());
        assertEquals(updateReviewRequest.getRating(), capturedReviewForSave.getRating());
        assertEquals(updateReviewRequest.getComment(), capturedReviewForSave.getComment());
        assertEquals(expectedReviewResponse.getCreatedAt(), capturedReviewForSave.getCreatedAt());

        verify(reviewMapper).mapToReviewResponse(capturedReviewForSave);
    }
}
