package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameRequest;
import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.model.Game;

import java.util.List;

public interface GameService {

    Game createGame(GameRequest gameRequest);

    GameResponse getGameById(Long gameId);

    List<GameResponse> listAllGames();

    Game updateGame(GameRequest gameRequest, Long gameId);

    void deleteGame(Long id);

}
