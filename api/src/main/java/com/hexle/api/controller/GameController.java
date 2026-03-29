package com.hexle.api.controller;

import com.hexle.api.model.GameState;
import com.hexle.api.model.GuessRequest;
import com.hexle.api.model.GuessResponse;
import com.hexle.api.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public ResponseEntity<GameState> startGame() {
        return ResponseEntity.ok(gameService.startGame());
    }

    @PostMapping("/guess")
    public ResponseEntity<?> submitGuess(@RequestBody @Valid GuessRequest request) {
        try {
            GuessResponse response = gameService.submitGuess(request.getGameId(), request.getGuess());
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{gameId}")
    public ResponseEntity<?> getStatus(@PathVariable String gameId) {
        try {
            return ResponseEntity.ok(gameService.getGameState(gameId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
