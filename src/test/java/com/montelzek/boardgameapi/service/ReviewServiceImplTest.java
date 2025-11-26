package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.ReviewRequest;
import com.montelzek.boardgameapi.dto.ReviewResponse;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private ReviewResponse reviewResponse;
    private ReviewRequest reviewRequest;

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

        reviewResponse = new ReviewResponse(
                1L,
                game.getId(),
                game.getTitle(),
                user.getId(),
                user.getEmail(),
                7,
                "Test Comment",
                review.getCreatedAt()
        );

        reviewRequest = new ReviewRequest(9, "Excellent game!");
    }

    @Test
    void getReviewsByGameIdTest_whenGameExistsAndHaveOneReviews_shouldReturnListOfReviewResponses() {
        // Arrange
        List<Review> reviews = List.of(review);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(reviewRepository.findByGame(game)).thenReturn(reviews);
        when(reviewMapper.mapToReviewResponse(any(Review.class))).thenReturn(reviewResponse);

        // Act
        List<ReviewResponse> actualResponse = reviewService.getReviewsByGameId(game.getId());

        // Assert
        assertNotNull(actualResponse);
        assertEquals(actualResponse.size(), reviews.size());
        ReviewResponse firstReview = actualResponse.getFirst();
        assertEquals(review.getId(), firstReview.id());
        assertEquals(review.getGame().getId(), firstReview.gameId());
        assertEquals(review.getGame().getTitle(), firstReview.gameTitle());
        assertEquals(review.getUser().getId(), firstReview.userId());
        assertEquals(review.getUser().getEmail(), firstReview.email());
        assertEquals(review.getRating(), firstReview.rating());
        assertEquals(review.getComment(), firstReview.comment());
        assertEquals(review.getCreatedAt(), firstReview.createdAt());

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
        List<ReviewResponse> actualResponse = reviewService.getReviewsByGameId(game.getId());

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
                .rating(reviewRequest.rating())
                .comment(reviewRequest.comment())
                .createdAt(fixedTestTime)
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponse expectedResponse = new ReviewResponse(
                savedReview.getId(),
                savedReview.getGame().getId(),
                savedReview.getGame().getTitle(),
                savedReview.getUser().getId(),
                savedReview.getUser().getEmail(),
                savedReview.getRating(),
                savedReview.getComment(),
                savedReview.getCreatedAt()
        );

        when(reviewMapper.mapToReviewResponse(savedReview)).thenReturn(expectedResponse);

        // Act
        ReviewResponse actualResponse = reviewService.createReview(gameId, reviewRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.userId(), actualResponse.userId());
        assertEquals(expectedResponse.email(), actualResponse.email());
        assertEquals(expectedResponse.gameId(), actualResponse.gameId());
        assertEquals(expectedResponse.gameTitle(), actualResponse.gameTitle());
        assertEquals(expectedResponse.rating(), actualResponse.rating());
        assertEquals(expectedResponse.comment(), actualResponse.comment());
        assertEquals(expectedResponse.createdAt(), actualResponse.createdAt());

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
        assertEquals(reviewRequest.rating(), capturedReviewForSave.getRating());
        assertEquals(reviewRequest.comment(), capturedReviewForSave.getComment());
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

        ReviewRequest updateReviewRequest = new ReviewRequest(5, "Updated comment");

        when(reviewRepository.findById(reviewIdToUpdate)).thenReturn(Optional.of(review));
        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        ReviewResponse expectedReviewResponse = new ReviewResponse(
                review.getId(),
                game.getId(),
                game.getTitle(),
                user.getId(),
                user.getEmail(),
                updateReviewRequest.rating(),
                updateReviewRequest.comment(),
                review.getCreatedAt()
        );
        when(reviewMapper.mapToReviewResponse(any(Review.class))).thenReturn(expectedReviewResponse);

        // Act
        ReviewResponse actualResponse = reviewService.updateReview(reviewIdToUpdate, updateReviewRequest);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedReviewResponse.id(), actualResponse.id());
        assertEquals(expectedReviewResponse.userId(), actualResponse.userId());
        assertEquals(expectedReviewResponse.email(), actualResponse.email());
        assertEquals(expectedReviewResponse.gameId(), actualResponse.gameId());
        assertEquals(expectedReviewResponse.gameTitle(), actualResponse.gameTitle());
        assertEquals(updateReviewRequest.rating(), actualResponse.rating());
        assertEquals(updateReviewRequest.comment(), actualResponse.comment());
        assertEquals(expectedReviewResponse.createdAt(), actualResponse.createdAt());

        verify(reviewRepository).findById(reviewIdToUpdate);
        verify(userService).getCurrentUserId();

        ArgumentCaptor<Review> reviewArgumentCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgumentCaptor.capture());
        Review capturedReviewForSave = reviewArgumentCaptor.getValue();

        assertEquals(review.getId(), capturedReviewForSave.getId());
        assertEquals(user, capturedReviewForSave.getUser());
        assertEquals(game, capturedReviewForSave.getGame());
        assertEquals(updateReviewRequest.rating(), capturedReviewForSave.getRating());
        assertEquals(updateReviewRequest.comment(), capturedReviewForSave.getComment());
        assertEquals(expectedReviewResponse.createdAt(), capturedReviewForSave.getCreatedAt());

        verify(reviewMapper).mapToReviewResponse(capturedReviewForSave);
    }

    @Test
    void updateReviewTest_whenReviewNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentReviewId = -1L;
        when(reviewRepository.findById(nonExistentReviewId)).thenReturn(Optional.empty());

        ReviewRequest updateReviewRequest = new ReviewRequest(5, "Updated comment");

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                reviewService.updateReview(nonExistentReviewId, updateReviewRequest));

        assertEquals("Review not found with id: " + nonExistentReviewId, exception.getMessage());
        verify(reviewRepository).findById(nonExistentReviewId);
        verify(userService, never()).getCurrentUserId();
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reviewMapper, never()).mapToReviewResponse(any(Review.class));
    }

    @Test
    void updateReviewTest_whenUserIsNotOwner_shouldThrowAccessDeniedException() {
        // Arrange
        Long reviewIdToUpdate = review.getId();
        Long nonOwnerUserId = 2L;

        ReviewRequest updateReviewRequest = new ReviewRequest(5, "Updated comment");

        when(reviewRepository.findById(reviewIdToUpdate)).thenReturn(Optional.of(review));
        when(userService.getCurrentUserId()).thenReturn(nonOwnerUserId);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                reviewService.updateReview(reviewIdToUpdate, updateReviewRequest));

        assertEquals("You are not authorized to update this review", exception.getMessage());
        verify(reviewRepository).findById(reviewIdToUpdate);
        verify(userService).getCurrentUserId();
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reviewMapper, never()).mapToReviewResponse(any(Review.class));
    }

    @Test
    void deleteReviewTest_whenReviewExistsAndUserIsOwner_shouldDeleteReview() {
        // Arrange
        Long reviewIdToDelete = review.getId();
        Long currentUserId = user.getId();

        when(reviewRepository.findById(reviewIdToDelete)).thenReturn(Optional.of(review));
        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        doNothing().when(reviewRepository).delete(review);

        // Act
        reviewService.deleteReview(reviewIdToDelete);

        // Assert
        verify(reviewRepository).findById(reviewIdToDelete);
        verify(userService).getCurrentUserId();
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReviewTest_whenReviewExistsAndUserIsAdmin_shouldDeleteReview() {
        // Arrange
        Long reviewIdToDelete = review.getId();
        Long adminCurrentUserId = 2L;

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(reviewRepository.findById(reviewIdToDelete)).thenReturn(Optional.of(review));
        when(userService.getCurrentUserId()).thenReturn(adminCurrentUserId);

        // Act
        reviewService.deleteReview(reviewIdToDelete);

        // Assert
        verify(reviewRepository).findById(reviewIdToDelete);
        verify(userService).getCurrentUserId();
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReviewTest_whenReviewNotExist_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentReviewId = -1L;

        when(reviewRepository.findById(nonExistentReviewId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                reviewService.deleteReview(nonExistentReviewId));

        assertEquals("Review not found with id: " + nonExistentReviewId, exception.getMessage());
        verify(reviewRepository).findById(nonExistentReviewId);
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void deleteReviewTest_whenUserIsNotOwnerAndNotAdmin_shouldThrowAccessDeniedException() {
        // Arrange
        Long reviewIdToDelete = review.getId();
        Long currentUserId = 10L;

        when(reviewRepository.findById(reviewIdToDelete)).thenReturn(Optional.of(review));
        when(userService.getCurrentUserId()).thenReturn(currentUserId);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                reviewService.deleteReview(reviewIdToDelete));

        assertEquals("You are not authorized to delete this review", exception.getMessage());
        verify(reviewRepository).findById(reviewIdToDelete);
        verify(reviewRepository, never()).delete(any());
    }
}
