package com.hexle.api.model;

public class StartGameResponse {

    private String gameId;
    private Integer challengerAttempts;

    public StartGameResponse(String gameId, Integer challengerAttempts) {
        this.gameId = gameId;
        this.challengerAttempts = challengerAttempts;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Integer getChallengerAttempts() {
        return challengerAttempts;
    }

    public void setChallengerAttempts(Integer challengerAttempts) {
        this.challengerAttempts = challengerAttempts;
    }
}
