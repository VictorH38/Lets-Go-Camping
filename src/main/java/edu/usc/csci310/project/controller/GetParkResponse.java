package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Park;

public class GetParkResponse {
    private Park data;

    public Park getData() {
        return data;
    }

    public void setData(Park data) {
        this.data = data;
    }

    public GetParkResponse(Park park) {
        this.data = park;
    }
}
