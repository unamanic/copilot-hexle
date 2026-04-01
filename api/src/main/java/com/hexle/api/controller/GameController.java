package com.hexle.api.controller;

import com.hexle.api.model.CreateChallengeRequest;
import com.hexle.api.model.CreateChallengeResponse;
import com.hexle.api.model.GameState;
import com.hexle.api.model.GuessRequest;
import com.hexle.api.model.GuessResponse;
import com.hexle.api.model.StartGameRequest;
import com.hexle.api.model.StartGameResponse;
import com.hexle.api.service.ChallengeService;
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
    private final ChallengeService challengeService;

    public GameController(GameService gameService, ChallengeService challengeService) {
        this.gameService = gameService;
        this.challengeService = challengeService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestBody(required = false) StartGameRequest request) {
        String token = (request != null) ? request.getChallengeToken() : null;

        if (token != null && !token.isBlank()) {
            try {
                ChallengeService.ChallengeData data = challengeService.decodeToken(token);
                GameState state = gameService.startGameWithWord(data.word());
                return ResponseEntity.ok(new StartGameResponse(state.getGameId(), data.challengerAttempts()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        GameState state = gameService.startGame();
        return ResponseEntity.ok(new StartGameResponse(state.getGameId(), null));
    }

    @PostMapping("/challenge/create")
    public ResponseEntity<?> createChallenge(@RequestBody @Valid CreateChallengeRequest request) {
        try {
            String word = gameService.getWordForGame(request.getGameId());
            int guessCount = gameService.getGuessCountForGame(request.getGameId());
            String token = challengeService.createToken(word, guessCount);
            return ResponseEntity.ok(new CreateChallengeResponse(token));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
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
