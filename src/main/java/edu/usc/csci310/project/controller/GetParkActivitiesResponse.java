package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Activity;

import java.util.List;

public class GetParkActivitiesResponse {
    private List<Activity> data;

    public GetParkActivitiesResponse(List<Activity> activities) {
        this.data = activities;
    }

    public List<Activity> getData() {
        return data;
    }

    public void setData(List<Activity> data) {
        this.data = data;
    }
}
