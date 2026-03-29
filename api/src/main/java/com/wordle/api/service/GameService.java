package com.wordle.api.service;

import com.wordle.api.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {

    private static final String KEY_PREFIX = "game:";

    private final RedisTemplate<String, GameState> redisTemplate;
    private final WordService wordService;

    @Value("${game.ttl.minutes:30}")
    private long ttlMinutes;

    public GameService(RedisTemplate<String, GameState> redisTemplate, WordService wordService) {
        this.redisTemplate = redisTemplate;
        this.wordService = wordService;
    }

    public GameState startGame() {
        GameState state = new GameState();
        state.setGameId(UUID.randomUUID().toString());
        state.setSolution(wordService.getRandomWord());
        state.setStatus(GameStatus.IN_PROGRESS);
        save(state);
        return sanitize(state);
    }

    public GuessResponse submitGuess(String gameId, String guess) {
        GameState state = load(gameId);
        if (state == null) {
            throw new NoSuchElementException("Game not found: " + gameId);
        }
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is already over");
        }

        String normalizedGuess = guess.toLowerCase();

        if (normalizedGuess.length() != 6) {
            throw new IllegalArgumentException("Guess must be exactly 6 letters");
        }
        if (!wordService.isValidWord(normalizedGuess)) {
            throw new IllegalArgumentException("Not a valid word: " + guess);
        }

        List<LetterFeedback> feedback = computeFeedback(normalizedGuess, state.getSolution());
        state.getGuesses().add(normalizedGuess);
        state.getFeedback().add(feedback);

        boolean isWin = normalizedGuess.equals(state.getSolution());
        boolean isLose = !isWin && state.getGuesses().size() >= state.getMaxGuesses();

        if (isWin) state.setStatus(GameStatus.WIN);
        else if (isLose) state.setStatus(GameStatus.LOSE);

        save(state);

        GuessResponse response = new GuessResponse();
        response.setFeedback(feedback);
        response.setStatus(state.getStatus());
        response.setGuessNumber(state.getGuesses().size());
        response.setGameOver(state.getStatus() != GameStatus.IN_PROGRESS);

        if (response.isGameOver()) {
            response.setSolution(state.getSolution());
            response.setMessage(isWin ? "Congratulations! You won!" : "Game over! Better luck next time.");
        } else {
            response.setMessage("Guess " + state.getGuesses().size() + " of " + state.getMaxGuesses());
        }

        return response;
    }

    public GameState getGameState(String gameId) {
        GameState state = load(gameId);
        if (state == null) {
            throw new NoSuchElementException("Game not found: " + gameId);
        }
        return sanitize(state);
    }

    /**
     * Computes per-letter Wordle feedback, correctly handling duplicate letters.
     * Pass 1: mark CORRECT letters (right letter, right position).
     * Pass 2: for remaining letters, mark PRESENT if the letter still appears
     *         in unmatched positions of the solution; otherwise ABSENT.
     */
    List<LetterFeedback> computeFeedback(String guess, String solution) {
        LetterFeedback[] result = new LetterFeedback[6];
        int[] solutionCounts = new int[26];

        for (int i = 0; i < 6; i++) {
            if (guess.charAt(i) == solution.charAt(i)) {
                result[i] = LetterFeedback.CORRECT;
            } else {
                solutionCounts[solution.charAt(i) - 'a']++;
            }
        }

        for (int i = 0; i < 6; i++) {
            if (result[i] != null) continue;
            char c = guess.charAt(i);
            int idx = c - 'a';
            if (solutionCounts[idx] > 0) {
                result[i] = LetterFeedback.PRESENT;
                solutionCounts[idx]--;
            } else {
                result[i] = LetterFeedback.ABSENT;
            }
        }

        return Arrays.asList(result);
    }

    private void save(GameState state) {
        redisTemplate.opsForValue().set(KEY_PREFIX + state.getGameId(), state, ttlMinutes, TimeUnit.MINUTES);
    }

    private GameState load(String gameId) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + gameId);
    }

    /** Returns a copy of GameState without the solution (unless game is over). */
    private GameState sanitize(GameState state) {
        GameState view = new GameState();
        view.setGameId(state.getGameId());
        view.setGuesses(state.getGuesses());
        view.setFeedback(state.getFeedback());
        view.setStatus(state.getStatus());
        view.setMaxGuesses(state.getMaxGuesses());
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            view.setSolution(state.getSolution());
        }
        return view;
    }
}

