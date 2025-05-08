package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    private GameMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new GameMapper();
    }

    @Test
    void shouldMapGameToGameResponse() {
        // Arrange
        Game game = Game.builder()
                .id(1L)
                .title("Test title")
                .description("Description")
                .minPlayers(2)
                .maxPlayers(8)
                .playTime(90)
                .publisher("Publisher")
                .releaseYear(2015)
                .build();
        // Act
        GameDTOs.GameResponse gameResponse = mapper.mapToGameResponse(game);
        //Assert
        assertEquals(gameResponse.getId(), game.getId());
        assertEquals(gameResponse.getTitle(), game.getTitle());
        assertEquals(gameResponse.getDescription(), game.getDescription());
        assertEquals(gameResponse.getMinPlayers(), game.getMinPlayers());
        assertEquals(gameResponse.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(gameResponse.getPlayTime(), game.getPlayTime());
        assertEquals(gameResponse.getPublisher(), game.getPublisher());
        assertEquals(gameResponse.getReleaseYear(), game.getReleaseYear());
    }

    @Test
    void shouldMapGameRequestToGame() {
        //Arrange
        Game game = Game.builder()
                .id(1L)
                .title("Test title")
                .description("Description")
                .minPlayers(2)
                .maxPlayers(8)
                .playTime(90)
                .publisher("Publisher")
                .releaseYear(2015)
                .build();
        GameDTOs.GameRequest gameRequest = GameDTOs.GameRequest.builder()
                .title("Test")
                .description("Fun game")
                .minPlayers(1)
                .maxPlayers(6)
                .playTime(120)
                .publisher("")
                .releaseYear(2015)
                .build();
        // Act
        mapper.mapGameRequestToGame(gameRequest, game);
        // Assert
        assertEquals(1L, game.getId());
        assertEquals(gameRequest.getTitle(), game.getTitle());
        assertEquals(gameRequest.getDescription(), game.getDescription());
        assertEquals(gameRequest.getMinPlayers(), game.getMinPlayers());
        assertEquals(gameRequest.getMaxPlayers(), game.getMaxPlayers());
        assertEquals(gameRequest.getPlayTime(), game.getPlayTime());
        assertEquals(gameRequest.getPublisher(), game.getPublisher());
        assertEquals(gameRequest.getReleaseYear(), game.getReleaseYear());
    }
}