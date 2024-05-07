package edu.usc.csci310.project.dto;

import edu.usc.csci310.project.domain.Park;

import java.util.List;

public class StateParksDto {
    private String state;
    private List<Park> parks;

    public StateParksDto(String state, List<Park> parks) {
        this.state = state;
        this.parks = parks;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Park> getParks() {
        return parks;
    }

    public void setParks(List<Park> parks) {
        this.parks = parks;
    }
}