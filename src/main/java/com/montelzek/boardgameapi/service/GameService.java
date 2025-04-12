package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.model.Game;

import java.util.List;

public interface GameService {

    Game createGame(GameDTOs.GameRequest gameRequest);

    GameDTOs.GameResponse getGameById(Long gameId);

    List<GameDTOs.GameResponse> listAllGames();

    Game updateGame(GameDTOs.GameRequest gameRequest, Long gameId);

    void deleteGame(Long id);

}
