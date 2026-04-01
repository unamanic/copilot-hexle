package com.hexle.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexle.api.model.*;
import com.hexle.api.service.ChallengeService;
import com.hexle.api.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @Mock
    private ChallengeService challengeService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private GameState testState;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new GameController(gameService, challengeService))
                .setValidator(validator)
                .build();

        testState = new GameState();
        testState.setGameId("test-game-id");
        testState.setStatus(GameStatus.IN_PROGRESS);
        testState.setGuesses(new ArrayList<>());
        testState.setFeedback(new ArrayList<>());
        testState.setMaxGuesses(6);
    }

    @Test
    void startGameReturns200WithGameState() throws Exception {
        when(gameService.startGame()).thenReturn(testState);

        mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("test-game-id"));
    }

    @Test
    void submitValidGuessReturns200() throws Exception {
        GuessResponse guessResponse = new GuessResponse();
        guessResponse.setFeedback(List.of(
                LetterFeedback.ABSENT, LetterFeedback.ABSENT, LetterFeedback.ABSENT,
                LetterFeedback.ABSENT, LetterFeedback.ABSENT, LetterFeedback.ABSENT));
        guessResponse.setStatus(GameStatus.IN_PROGRESS);
        guessResponse.setGuessNumber(1);
        guessResponse.setGameOver(false);
        guessResponse.setMessage("Guess 1 of 6");
        when(gameService.submitGuess("test-game-id", "planet")).thenReturn(guessResponse);

        GuessRequest request = new GuessRequest();
        request.setGameId("test-game-id");
        request.setGuess("planet");

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback").isArray())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void submitInvalidWordReturns400() throws Exception {
        when(gameService.submitGuess(eq("test-game-id"), eq("xxxxzz")))
                .thenThrow(new IllegalArgumentException("Not a valid word: xxxxzz"));

        GuessRequest request = new GuessRequest();
        request.setGameId("test-game-id");
        request.setGuess("xxxxzz");

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void submitGuessTooShortReturns400() throws Exception {
        GuessRequest request = new GuessRequest();
        request.setGameId("test-game-id");
        request.setGuess("abc");

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatusReturns200ForValidGameId() throws Exception {
        when(gameService.getGameState("test-game-id")).thenReturn(testState);

        mockMvc.perform(get("/api/game/status/{gameId}", "test-game-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("test-game-id"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getStatusReturns404ForUnknownGameId() throws Exception {
        when(gameService.getGameState("nonexistent-game-id"))
                .thenThrow(new NoSuchElementException("Game not found"));

        mockMvc.perform(get("/api/game/status/{gameId}", "nonexistent-game-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitGuessWithMissingGameIdReturns400() throws Exception {
        GuessRequest request = new GuessRequest();
        request.setGuess("planet");

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
