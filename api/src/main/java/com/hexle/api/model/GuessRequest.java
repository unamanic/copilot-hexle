package com.hexle.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class GuessRequest {

    @NotBlank(message = "gameId must not be blank")
    private String gameId;

    @NotBlank(message = "guess must not be blank")
    @Size(min = 6, max = 6, message = "guess must be exactly 6 letters")
    @Pattern(regexp = "^[a-zA-Z]{6}$", message = "guess must contain only letters")
    private String guess;

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public String getGuess() { return guess; }
    public void setGuess(String guess) { this.guess = guess; }
}
