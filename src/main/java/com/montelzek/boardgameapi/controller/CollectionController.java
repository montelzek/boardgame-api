package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.CollectionRequest;
import com.montelzek.boardgameapi.dto.GameResponse;
import com.montelzek.boardgameapi.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping
    public ResponseEntity<List<GameResponse>> getUserCollection(@PathVariable Long userId) {
        List<GameResponse> collection = collectionService.getUserCollection(userId);
        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<?> addGameToCollection(@PathVariable Long userId,
                                                    @Valid @RequestBody CollectionRequest collectionRequest) {
        collectionService.addGameToCollection(userId, collectionRequest.gameId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Game with ID " + collectionRequest.gameId()
                + " added to user " + userId + "'s collection successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> removeGameFromCollection(@PathVariable Long userId, @PathVariable Long gameId) {

        collectionService.removeGameFromCollection(userId, gameId);
        return ResponseEntity.noContent().build();
    }
}
