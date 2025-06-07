package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Test
    void getGameByIdTest_whenGameNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentId = 99L;
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                gameService.getGameById(nonExistentId));

        assertEquals("Game not found with id: " + nonExistentId, exception.getMessage());
        verify(gameRepository).findById(nonExistentId);
    }

    @Test
    void listAllGamesTest_whenGamesExist_shouldReturnListOfGameResponses() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(List.of(game1));
        when(gameMapper.mapToGameResponse(game1)).thenReturn(gameResponse);

        // Act
        List<GameDTOs.GameResponse> result = gameService.listAllGames();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(gameResponse.getTitle(), result.getFirst().getTitle());

        verify(gameRepository).findAll();
        verify(gameMapper, times(1)).mapToGameResponse(game1);
    }

    @Test
    void listAllGamesTest_whenNoGames_shouldReturnEmptyList() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<GameDTOs.GameResponse> result = gameService.listAllGames();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(gameRepository).findAll();
        verify(gameMapper, never()).mapToGameResponse(any());
    }

    @Test
    void updateGameTest_whenGameFound_shouldUpdateAndReturnGame() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game1));
        when(gameRepository.save(any(Game.class))).thenReturn(game1);

        // Act
        Game updatedGame = gameService.updateGame(gameRequest, 1L);

        // Assert
        assertNotNull(updatedGame);
        verify(gameRepository).findById(1L);
        verify(gameMapper).mapGameRequestToGame(gameRequest, game1);
        verify(gameRepository).save(gameArgumentCaptor.capture());

        assertSame(game1, gameArgumentCaptor.getValue());
    }

    @Test
    void updateGameTest_whenGameNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentId = 99L;
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                gameService.updateGame(gameRequest, nonExistentId));

        assertEquals("Game not found with id: " + nonExistentId, exception.getMessage());
        verify(gameRepository).findById(nonExistentId);
        verify(gameRepository, never()).save(any());
        verify(gameMapper, never()).mapGameRequestToGame(any(), any());
    }
}
