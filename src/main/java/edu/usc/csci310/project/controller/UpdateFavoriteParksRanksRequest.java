package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UpdateFavoriteParksRanksRequest {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("park_ids")
    private List<Long> parkIds;

    public UpdateFavoriteParksRanksRequest() {
    }

    public UpdateFavoriteParksRanksRequest(Long userId, List<Long> parkIds) {
        this.userId = userId;
        this.parkIds = parkIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getParkIds() {
        return parkIds;
    }

    public void setParkIds(List<Long> parkIds) {
        this.parkIds = parkIds;
    }
}
