package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameResponse;

import java.util.List;

public interface CollectionService {

    List<GameResponse> getUserCollection(Long userId);

    void addGameToCollection(Long userId, Long gameId);

    void removeGameFromCollection(Long userId, Long gameId);
}
