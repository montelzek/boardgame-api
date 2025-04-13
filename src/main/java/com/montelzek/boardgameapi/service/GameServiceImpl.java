package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public Game createGame(GameDTOs.GameRequest gameRequest) {

        Game newGame = new Game();
        gameMapper.mapGameRequestToGame(gameRequest, newGame);

        return gameRepository.save(newGame);
    }

    @Override
    public GameDTOs.GameResponse getGameById(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        List<ReviewDTOs.ReviewResponse> reviews = game.getReviews().stream()
                .map(reviewMapper::mapToReviewResponse)
                .collect(Collectors.toList());

        GameDTOs.GameResponse gameResponse = gameMapper.mapToGameResponse(game);

        return GameDTOs.GameResponse.builder()
                .id(gameResponse.getId())
                .title(gameResponse.getTitle())
                .description(gameResponse.getDescription())
                .minPlayers(gameResponse.getMinPlayers())
                .maxPlayers(gameResponse.getMaxPlayers())
                .playTime(gameResponse.getPlayTime())
                .publisher(gameResponse.getPublisher())
                .releaseYear(gameResponse.getReleaseYear())
                .reviews(reviews)
                .build();
    }


    @Override
    public List<GameDTOs.GameResponse> listAllGames() {
        return gameRepository.findAll().stream()
                .map(gameMapper::mapToGameResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Game updateGame(GameDTOs.GameRequest gameRequest, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        gameMapper.mapGameRequestToGame(gameRequest, game);

        return gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));

        gameRepository.delete(game);
    }
}
