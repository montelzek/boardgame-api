package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.GameRequest;
import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public GameResponse mapToGameResponse(Game game) {
        return new GameResponse(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                game.getMinPlayers(),
                game.getMaxPlayers(),
                game.getPlayTime(),
                game.getPublisher(),
                game.getReleaseYear(),
                null,
                null
        );
    }

    public void mapGameRequestToGame(GameRequest gameRequest, Game game) {
        game.setTitle(gameRequest.title());
        game.setDescription(gameRequest.description());
        game.setMinPlayers(gameRequest.minPlayers());
        game.setMaxPlayers(gameRequest.maxPlayers());
        game.setPlayTime(gameRequest.playTime());
        game.setPublisher(gameRequest.publisher());
        game.setReleaseYear(gameRequest.releaseYear());
    }
}
