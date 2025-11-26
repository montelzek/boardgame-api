package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.GameRequest;
import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    private GameMapper mapper;
    private Game game;

    @BeforeEach
    void setUp() {
        mapper = new GameMapper();

        game = Game.builder()
                .id(1L)
                .title("Test title")
                .description("Description")
                .minPlayers(2)
                .maxPlayers(8)
                .playTime(90)
                .publisher("Publisher")
                .releaseYear(2015)
                .build();
    }

    @Test
    void shouldMapGameToGameResponse() {
        // Act
        GameResponse gameResponse = mapper.mapToGameResponse(game);
        //Assert
        assertEquals(gameResponse.id(), game.getId());
        assertEquals(gameResponse.title(), game.getTitle());
        assertEquals(gameResponse.description(), game.getDescription());
        assertEquals(gameResponse.minPlayers(), game.getMinPlayers());
        assertEquals(gameResponse.maxPlayers(), game.getMaxPlayers());
        assertEquals(gameResponse.playTime(), game.getPlayTime());
        assertEquals(gameResponse.publisher(), game.getPublisher());
        assertEquals(gameResponse.releaseYear(), game.getReleaseYear());
    }

    @Test
    void shouldMapGameRequestToGame() {
        GameRequest gameRequest = new GameRequest(
                "Test",
                "Fun game",
                1,
                6,
                120,
                "",
                2015
        );
        // Act
        mapper.mapGameRequestToGame(gameRequest, game);
        // Assert
        assertEquals(1L, game.getId());
        assertEquals(gameRequest.title(), game.getTitle());
        assertEquals(gameRequest.description(), game.getDescription());
        assertEquals(gameRequest.minPlayers(), game.getMinPlayers());
        assertEquals(gameRequest.maxPlayers(), game.getMaxPlayers());
        assertEquals(gameRequest.playTime(), game.getPlayTime());
        assertEquals(gameRequest.publisher(), game.getPublisher());
        assertEquals(gameRequest.releaseYear(), game.getReleaseYear());
    }
}