package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.dto.ReviewDTOs;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.Review;
import com.montelzek.boardgameapi.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;

    @Override
    public Game createGame(GameDTOs.GameRequest gameRequest) {

        Game newGame = new Game();
        newGame.setTitle(gameRequest.getTitle());
        newGame.setDescription(gameRequest.getDescription());
        newGame.setMinPlayers(gameRequest.getMinPlayers());
        newGame.setMaxPlayers(gameRequest.getMaxPlayers());
        newGame.setPlayTime(gameRequest.getPlayTime());
        newGame.setPublisher(gameRequest.getPublisher());
        newGame.setReleaseYear(gameRequest.getReleaseYear());

        return gameRepository.save(newGame);
    }

    @Override
    public GameDTOs.GameResponse getGameById(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        List<ReviewDTOs.ReviewResponse> reviews = game.getReviews().stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());

        GameDTOs.GameResponse gameResponse = mapToGameResponse(game);

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
                .map(this::mapToGameResponse)
                .collect(Collectors.toList());
    }

    private ReviewDTOs.ReviewResponse mapToReviewResponse(Review review) {
        return ReviewDTOs.ReviewResponse.builder()
                .id(review.getId())
                .gameId(review.getGame().getId())
                .gameTitle(review.getGame().getTitle())
                .userId(review.getUser().getId())
                .email(review.getUser().getEmail())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private GameDTOs.GameResponse mapToGameResponse(Game game) {
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
}
