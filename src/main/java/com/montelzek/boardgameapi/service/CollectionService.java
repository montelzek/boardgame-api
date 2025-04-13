package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;

import java.util.List;

public interface CollectionService {

    List<GameDTOs.GameResponse> getUserCollection(Long userId);

    void addGameToCollection(Long userId, Long gameId);

    void removeGameFromCollection(Long userId, Long gameId);
}
