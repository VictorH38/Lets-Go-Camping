package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateFavoriteParkRequest {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("park_id")
    private Long parkId;

    public CreateFavoriteParkRequest() {
    }

    public CreateFavoriteParkRequest(Long userId, Long parkId) {
        this.userId = userId;
        this.parkId = parkId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParkId() {
        return parkId;
    }

    public void setParkId(Long parkId) {
        this.parkId = parkId;
    }

}
