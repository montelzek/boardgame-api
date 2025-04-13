package com.montelzek.boardgameapi.service;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.exception.ResourceNotFoundException;
import com.montelzek.boardgameapi.mapper.GameMapper;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.model.User;
import com.montelzek.boardgameapi.repository.GameRepository;
import com.montelzek.boardgameapi.repository.ReviewRepository;
import com.montelzek.boardgameapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService{

    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final GameMapper gameMapper;

    @Override
    public List<GameDTOs.GameResponse> getUserCollection(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return user.getGames().stream()
                .map(gameMapper::mapToGameResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addGameToCollection(Long userId, Long gameId) {

        User user = getAuthorizedUser(userId);
        Game game = getGameById(gameId);

        Set<Game> games = user.getGames();
        games.add(game);
        user.setGames(games);
        userRepository.save(user);
    }

    @Override
    public void removeGameFromCollection(Long userId, Long gameId) {

        User user = getAuthorizedUser(userId);
        Game game = getGameById(gameId);

        Set<Game> games = user.getGames();
        games.remove(game);
        user.setGames(games);
        userRepository.save(user);
    }

    private User getAuthorizedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!userService.getCurrentUserId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to modify this collection");
        }

        return user;
    }

    private Game getGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
    }
}
