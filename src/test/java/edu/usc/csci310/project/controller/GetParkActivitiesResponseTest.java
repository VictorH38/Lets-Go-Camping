package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Activity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetParkActivitiesResponseTest {

    @Test
    void testConstructorAndGetters() {
        Activity activity1 = new Activity();
        activity1.setName("Hiking");
        Activity activity2 = new Activity();
        activity2.setName("Fishing");
        List<Activity> activities = Arrays.asList(activity1, activity2);

        GetParkActivitiesResponse response = new GetParkActivitiesResponse(activities);

        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals(activity1, response.getData().get(0));
        assertEquals(activity2, response.getData().get(1));
    }

    @Test
    void testSetData() {
        Activity activity1 = new Activity();
        activity1.setName("Camping");
        List<Activity> initialActivities = Arrays.asList(activity1);

        Activity activity2 = new Activity();
        activity2.setName("Bird Watching");
        List<Activity> newActivities = Arrays.asList(activity2);

        GetParkActivitiesResponse response = new GetParkActivitiesResponse(initialActivities);

        response.setData(newActivities);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(activity2, response.getData().get(0));
    }

}