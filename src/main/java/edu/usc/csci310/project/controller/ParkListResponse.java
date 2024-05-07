package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.dto.SearchParkDto;

import java.util.List;

public class ParkListResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ParkListResponse(List<SearchParkDto> parks) {
        Data data = new Data();
        data.setParks(parks);
        this.data = data;
    }

    public static class Data {
        private List<SearchParkDto> parks;

        public List<SearchParkDto> getParks() {
            return parks;
        }

        public void setParks(List<SearchParkDto> parks) {
            this.parks = parks;
        }
    }
}