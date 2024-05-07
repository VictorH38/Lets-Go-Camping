package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Park;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetParkResponseTest {

    @Test
    public void testDataGetterAndSetter() {
        Park mockPark = new Park();
        mockPark.setId(468L);
        mockPark.setFullName("Yosemite National Park");

        GetParkResponse response = new GetParkResponse(mockPark);
        response.setData(response.getData());

        assertNotNull(response.getData(), "Data object should not be null");
        assertEquals(468L, response.getData().getId(), "The park ID should match the mock ID set");
        assertEquals("Yosemite National Park", response.getData().getFullName(), "The park name should match the mock name set");
    }
}
