package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.dto.FavoriteParkDto;

import java.util.List;

public class FavoriteParkListResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public FavoriteParkListResponse(List<FavoriteParkDto> parks) {
        Data data = new Data();
        data.setParks(parks);
        this.data = data;
    }

    public static class Data {
        private List<FavoriteParkDto> parks;

        public List<FavoriteParkDto> getParks() {
            return parks;
        }

        public void setParks(List<FavoriteParkDto> parks) {
            this.parks = parks;
        }
    }
}