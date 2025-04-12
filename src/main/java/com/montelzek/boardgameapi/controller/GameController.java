package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.GameDTOs;
import com.montelzek.boardgameapi.model.Game;
import com.montelzek.boardgameapi.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<GameDTOs.GameResponse>> getAllGames() {
        List<GameDTOs.GameResponse> games = gameService.listAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDTOs.GameResponse> getGameById(@PathVariable Long id) {
        GameDTOs.GameResponse game = gameService.getGameById(id);
        return ResponseEntity.ok(game);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTOs.GameResponse> createGame(@Valid @RequestBody GameDTOs.GameRequest gameRequest) {
        Game game = gameService.createGame(gameRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(game.getId())
                .toUri();

        return ResponseEntity.created(location).body(gameService.getGameById(game.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GameDTOs.GameResponse> updateGame(@PathVariable Long id,
            @Valid @RequestBody GameDTOs.GameRequest gameRequest) {

        Game game = gameService.updateGame(gameRequest, id);
        return ResponseEntity.ok(gameService.getGameById(game.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
