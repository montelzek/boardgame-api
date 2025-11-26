package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.CategoryDto;
import com.montelzek.boardgameapi.dto.GameRequest;
import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.dto.ReviewResponse;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.mapper.ReviewMapper;
import com.montelzek.boardgameapi.model.Category;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final ReviewMapper reviewMapper;

    @Override
    public Game createGame(GameRequest gameRequest) {

        Game newGame = new Game();
        gameMapper.mapGameRequestToGame(gameRequest, newGame);

        return gameRepository.save(newGame);
    }

    @Override
    public GameResponse getGameById(Long gameId) {
        Game game = gameRepository.findGameByIdWithCategories(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        Set<CategoryDto> categories = game.getCategories().stream()
                .map(this::mapToCategoryDto)
                .collect(Collectors.toSet());

        List<ReviewResponse> reviews = game.getReviews().stream()
                .map(reviewMapper::mapToReviewResponse)
                .collect(Collectors.toList());

        GameResponse baseResponse = gameMapper.mapToGameResponse(game);

        return new GameResponse(
                baseResponse.id(),
                baseResponse.title(),
                baseResponse.description(),
                baseResponse.minPlayers(),
                baseResponse.maxPlayers(),
                baseResponse.playTime(),
                baseResponse.publisher(),
                baseResponse.releaseYear(),
                categories,
                reviews
        );
    }


    @Override
    public List<GameResponse> listAllGames() {
        return gameRepository.findAll().stream()
                .map(gameMapper::mapToGameResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Game updateGame(GameRequest gameRequest, Long gameId) {
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

    private CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
