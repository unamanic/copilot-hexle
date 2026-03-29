package com.wordle.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordle.api.model.GameState;
import com.wordle.api.model.GuessRequest;
import com.wordle.api.service.GameService;
import com.wordle.api.service.WordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GameService gameService;

    @Autowired
    private WordService wordService;

    @Test
    void startGameReturns200WithGameState() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        GameState state = objectMapper.readValue(body, GameState.class);
        assertThat(state.getGameId()).isNotNull();
    }

    @Test
    void submitValidGuessReturns200() throws Exception {
        // Start a game first
        MvcResult startResult = mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andReturn();
        GameState state = objectMapper.readValue(
                startResult.getResponse().getContentAsString(), GameState.class);

        String validWord = wordService.getWordList().get(0);

        GuessRequest request = new GuessRequest();
        request.setGameId(state.getGameId());
        request.setGuess(validWord);

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback").isArray())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void submitInvalidWordReturns400() throws Exception {
        MvcResult startResult = mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andReturn();
        GameState state = objectMapper.readValue(
                startResult.getResponse().getContentAsString(), GameState.class);

        GuessRequest request = new GuessRequest();
        request.setGameId(state.getGameId());
        request.setGuess("xxxxzz");

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void submitGuessTooShortReturns400() throws Exception {
        MvcResult startResult = mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andReturn();
        GameState state = objectMapper.readValue(
                startResult.getResponse().getContentAsString(), GameState.class);

        GuessRequest request = new GuessRequest();
        request.setGameId(state.getGameId());
        request.setGuess("abc");

        mockMvc.perform(post("/api/game/guess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatusReturns200ForValidGameId() throws Exception {
        MvcResult startResult = mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andReturn();
        GameState state = objectMapper.readValue(
                startResult.getResponse().getContentAsString(), GameState.class);

        mockMvc.perform(get("/api/game/status/{gameId}", state.getGameId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(state.getGameId()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void getStatusReturns404ForUnknownGameId() throws Exception {
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
