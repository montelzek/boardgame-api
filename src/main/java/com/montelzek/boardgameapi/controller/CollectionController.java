package com.montelzek.boardgameapi.controller;

import com.montelzek.boardgameapi.dto.CollectionDTO;
import com.montelzek.boardgameapi.dto.GameDTOs;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping("/{userId}/collection")
    public ResponseEntity<List<GameDTOs.GameResponse>> getUserCollection(@PathVariable Long userId) {
        List<GameDTOs.GameResponse> collection = collectionService.getUserCollection(userId);
        return ResponseEntity.ok(collection);
    }

    @PostMapping("/{userId}/collection")
    public ResponseEntity<?> addGameToCollection(@PathVariable Long userId,
                                                    @Valid @RequestBody CollectionDTO.CollectionRequest collectionRequest) {
        collectionService.addGameToCollection(userId, collectionRequest.getGameId());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Game with ID " + collectionRequest.getGameId()
                + " added to user " + userId + "'s collection successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}/collection/{gameId}")
    public ResponseEntity<String> removeGameFromCollection(@PathVariable Long userId, @PathVariable Long gameId) {

        collectionService.removeGameFromCollection(userId, gameId);
        return new ResponseEntity<>("Game with ID " + gameId
                + " was successfully removed from the collection of user of id " + userId, HttpStatus.OK);
    }
}
