package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Amenity;
import edu.usc.csci310.project.dto.StateParksDto;

import java.util.List;

public class GetAmenitiesParksResponse {
    private List<Amenity> data;

    public GetAmenitiesParksResponse(List<Amenity> data) {
        this.data = data;
    }

    public List<Amenity> getData() {
        return data;
    }

    public void setData(List<Amenity> data) {
        this.data = data;
    }
}