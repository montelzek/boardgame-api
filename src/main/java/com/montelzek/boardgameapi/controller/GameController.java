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
}
