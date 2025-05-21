package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.dto.UserDTOs;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.model.Role;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Game game;
    private Review review;
    private GameDTOs.GameResponse gameResponse;
    private ReviewDTOs.ReviewResponse reviewResponse;

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
                .id(10L)
                .title("Test Game")
                .build();
        review = Review.builder()
                .id(20L)
                .comment("Test Review")
                .build();

        user.getGames().add(game);
        user.getReviews().add(review);

        gameResponse = GameDTOs.GameResponse.builder()
                .id(10L)
                .title("Test Game")
                .build();
        reviewResponse = ReviewDTOs.ReviewResponse.builder()
                .id(20L)
                .comment("Test Review")
                .build();
    }

    @Test
    void getUserByIdTest_whenUserExists_shouldReturnUserResponse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameMapper.mapToGameResponse(any(Game.class))).thenReturn(gameResponse);
        when(reviewMapper.mapToReviewResponse(any(Review.class))).thenReturn(reviewResponse);

        // Act
        UserDTOs.UserResponse actualResponse = userService.getUserById(1L);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(user.getId(), actualResponse.getId());
        assertEquals(user.getFullName(), actualResponse.getFullName());
        assertEquals(user.getEmail(), actualResponse.getEmail());
        assertEquals(user.getRole().name(), actualResponse.getRole());
        assertFalse(actualResponse.getCollection().isEmpty());
        assertEquals(gameResponse.getId(), actualResponse.getCollection().getFirst().getId());
        assertEquals(gameResponse.getTitle(), actualResponse.getCollection().getFirst().getTitle());
        assertFalse(actualResponse.getReviews().isEmpty());
        assertEquals(reviewResponse.getId(), actualResponse.getReviews().getFirst().getId());
        assertEquals(reviewResponse.getComment(), actualResponse.getReviews().getFirst().getComment());

        verify(userRepository).findById(1L);
        verify(gameMapper).mapToGameResponse(game);
        verify(reviewMapper).mapToReviewResponse(review);
    }
}
