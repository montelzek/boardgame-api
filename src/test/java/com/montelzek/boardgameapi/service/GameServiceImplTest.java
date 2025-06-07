package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private GameServiceImpl gameService;

    private Game game1;
    private GameDTOs.GameRequest gameRequest;
    private GameDTOs.GameResponse gameResponse;
    private Review review1;
    private ReviewDTOs.ReviewResponse reviewResponse;

    @Captor
    private ArgumentCaptor<Game> gameArgumentCaptor;

    @BeforeEach
    void setUp() {
        gameRequest = GameDTOs.GameRequest.builder()
                .title("Catan")
                .description("A game of trade and settlement.")
                .minPlayers(3)
                .maxPlayers(4)
                .playTime(90)
                .publisher("KOSMOS")
                .releaseYear(1995)
                .build();

        User user = User.builder().id(1L).email("user@test.com").build();

        review1 = Review.builder().id(10L).rating(9).comment("Classic!").user(user).build();

        game1 = Game.builder()
                .id(1L)
                .title("Catan")
                .description("A game of trade and settlement.")
                .minPlayers(3)
                .maxPlayers(4)
                .playTime(90)
                .publisher("KOSMOS")
                .releaseYear(1995)
                .reviews(new HashSet<>(Set.of(review1)))
                .build();

        review1.setGame(game1);

        reviewResponse = ReviewDTOs.ReviewResponse.builder()
                .id(review1.getId())
                .rating(review1.getRating())
                .comment(review1.getComment())
                .userId(user.getId())
                .email(user.getEmail())
                .build();

        gameResponse = GameDTOs.GameResponse.builder()
                .id(game1.getId())
                .title(game1.getTitle())
                .description(game1.getDescription())
                .minPlayers(game1.getMinPlayers())
                .maxPlayers(game1.getMaxPlayers())
                .playTime(game1.getPlayTime())
                .publisher(game1.getPublisher())
                .releaseYear(game1.getReleaseYear())
                .build();
    }

    @Test
    void createGameTest_shouldSaveAndReturnGame() {
        // Arrange
        when(gameRepository.save(any(Game.class))).thenReturn(game1);

        // Act
        Game savedGame = gameService.createGame(gameRequest);

        // Assert
        assertNotNull(savedGame);
        assertEquals("Catan", savedGame.getTitle());

        verify(gameMapper).mapGameRequestToGame(eq(gameRequest), any(Game.class));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void getGameByIdTest_whenGameFound_shouldReturnGameResponseWithReviews() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game1));
        when(gameMapper.mapToGameResponse(game1)).thenReturn(gameResponse);
        when(reviewMapper.mapToReviewResponse(review1)).thenReturn(reviewResponse);

        // Act
        GameDTOs.GameResponse actualResponse = gameService.getGameById(1L);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(game1.getId(), actualResponse.getId());
        assertEquals(game1.getTitle(), actualResponse.getTitle());
        assertFalse(actualResponse.getReviews().isEmpty());
        assertEquals(1, actualResponse.getReviews().size());
        assertEquals(reviewResponse.getId(), actualResponse.getReviews().getFirst().getId());
        assertEquals(reviewResponse.getComment(), actualResponse.getReviews().getFirst().getComment());

        verify(gameRepository).findById(1L);
        verify(gameMapper).mapToGameResponse(game1);
        verify(reviewMapper).mapToReviewResponse(review1);
    }
}
