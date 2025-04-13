package com.montelzek.boardgameapi.mapper;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public GameDTOs.GameResponse mapToGameResponse(Game game) {
        return GameDTOs.GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .minPlayers(game.getMinPlayers())
                .maxPlayers(game.getMaxPlayers())
                .playTime(game.getPlayTime())
                .publisher(game.getPublisher())
                .releaseYear(game.getReleaseYear())
                .build();
    }

    public void mapGameRequestToGame(GameDTOs.GameRequest gameRequest, Game game) {
        game.setTitle(gameRequest.getTitle());
        game.setDescription(gameRequest.getDescription());
        game.setMinPlayers(gameRequest.getMinPlayers());
        game.setMaxPlayers(gameRequest.getMaxPlayers());
        game.setPlayTime(gameRequest.getPlayTime());
        game.setPublisher(gameRequest.getPublisher());
        game.setReleaseYear(gameRequest.getReleaseYear());
    }
}
