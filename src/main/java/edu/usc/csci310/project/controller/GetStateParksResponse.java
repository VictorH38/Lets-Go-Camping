package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.dto.StateParksDto;
import java.util.List;

public class GetStateParksResponse {
    private List<StateParksDto> data;

    public GetStateParksResponse(List<StateParksDto> data) {
        this.data = data;
    }

    public List<StateParksDto> getData() {
        return data;
    }

    public void setData(List<StateParksDto> data) {
        this.data = data;
    }
}