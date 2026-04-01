package com.hexle.api.model;

import jakarta.validation.constraints.NotBlank;

public class CreateChallengeRequest {

    @NotBlank
    private String gameId;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
