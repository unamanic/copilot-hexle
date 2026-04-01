package com.hexle.api.model;

public class CreateChallengeResponse {

    private String token;

    public CreateChallengeResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
