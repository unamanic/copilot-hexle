package com.wordle.api.model;

import java.util.List;

public class GuessResponse {
    private List<LetterFeedback> feedback;
    private GameStatus status;
    private int guessNumber;
    private boolean gameOver;
    private String solution;
    private String message;

    public List<LetterFeedback> getFeedback() { return feedback; }
    public void setFeedback(List<LetterFeedback> feedback) { this.feedback = feedback; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public int getGuessNumber() { return guessNumber; }
    public void setGuessNumber(int guessNumber) { this.guessNumber = guessNumber; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
