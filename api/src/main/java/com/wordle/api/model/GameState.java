package com.wordle.api.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private String gameId;
    private String solution;
    private List<String> guesses = new ArrayList<>();
    private List<List<LetterFeedback>> feedback = new ArrayList<>();
    private GameStatus status = GameStatus.IN_PROGRESS;
    private int maxGuesses = 6;

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }

    public List<String> getGuesses() { return guesses; }
    public void setGuesses(List<String> guesses) { this.guesses = guesses; }

    public List<List<LetterFeedback>> getFeedback() { return feedback; }
    public void setFeedback(List<List<LetterFeedback>> feedback) { this.feedback = feedback; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public int getMaxGuesses() { return maxGuesses; }
    public void setMaxGuesses(int maxGuesses) { this.maxGuesses = maxGuesses; }
}
