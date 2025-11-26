package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameRequest;
import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.dto.ReviewResponse;
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
    private GameRequest gameRequest;
    private GameResponse gameResponse;
    private Review review1;
    private ReviewResponse reviewResponse;

    @Captor
    private ArgumentCaptor<Game> gameArgumentCaptor;

    @BeforeEach
    void setUp() {
        gameRequest = new GameRequest(
                "Catan",
                "A game of trade and settlement.",
                3,
                4,
                90,
                "KOSMOS",
                1995
        );

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

        reviewResponse = new ReviewResponse(
                review1.getId(),
                game1.getId(),
                game1.getTitle(),
                user.getId(),
                user.getEmail(),
                review1.getRating(),
                review1.getComment(),
                review1.getCreatedAt()
        );

        gameResponse = new GameResponse(
                game1.getId(),
                game1.getTitle(),
                game1.getDescription(),
                game1.getMinPlayers(),
                game1.getMaxPlayers(),
                game1.getPlayTime(),
                game1.getPublisher(),
                game1.getReleaseYear(),
                Collections.emptySet(),
                List.of(reviewResponse)
        );
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
        when(gameRepository.findGameByIdWithCategories(1L)).thenReturn(Optional.of(game1));
        when(gameMapper.mapToGameResponse(game1)).thenReturn(gameResponse);
        when(reviewMapper.mapToReviewResponse(review1)).thenReturn(reviewResponse);

        // Act
        GameResponse actualResponse = gameService.getGameById(1L);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(game1.getId(), actualResponse.id());
        assertEquals(game1.getTitle(), actualResponse.title());
        assertFalse(actualResponse.reviews().isEmpty());
        assertEquals(1, actualResponse.reviews().size());
        assertEquals(reviewResponse.id(), actualResponse.reviews().getFirst().id());
        assertEquals(reviewResponse.comment(), actualResponse.reviews().getFirst().comment());

        verify(gameRepository).findGameByIdWithCategories(1L);
        verify(gameMapper).mapToGameResponse(game1);
        verify(reviewMapper).mapToReviewResponse(review1);
    }

    @Test
    void getGameByIdTest_whenGameNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentId = 99L;
        when(gameRepository.findGameByIdWithCategories(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                gameService.getGameById(nonExistentId));

        assertEquals("Game not found with id: " + nonExistentId, exception.getMessage());
        verify(gameRepository).findGameByIdWithCategories(nonExistentId);
    }

    @Test
    void listAllGamesTest_whenGamesExist_shouldReturnListOfGameResponses() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(List.of(game1));
        when(gameMapper.mapToGameResponse(game1)).thenReturn(gameResponse);

        // Act
        List<GameResponse> result = gameService.listAllGames();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(gameResponse.title(), result.getFirst().title());

        verify(gameRepository).findAll();
        verify(gameMapper, times(1)).mapToGameResponse(game1);
    }

    @Test
    void listAllGamesTest_whenNoGames_shouldReturnEmptyList() {
        // Arrange
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<GameResponse> result = gameService.listAllGames();

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

    @Test
    void deleteGameTest_whenGameFound_shouldDeleteGame() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(game1));
        doNothing().when(gameRepository).delete(any(Game.class));

        // Act
        gameService.deleteGame(1L);

        // Assert
        verify(gameRepository).findById(1L);
        verify(gameRepository).delete(gameArgumentCaptor.capture());

        assertEquals(game1.getId(), gameArgumentCaptor.getValue().getId());
        assertSame(game1, gameArgumentCaptor.getValue());
    }

    @Test
    void deleteGameTest_whenGameNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistentId = 99L;
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                gameService.deleteGame(nonExistentId));

        assertEquals("Game not found with id: " + nonExistentId, exception.getMessage());
        verify(gameRepository).findById(nonExistentId);
        verify(gameRepository, never()).delete(any());
    }
}
