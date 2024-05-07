package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.dto.SearchParkDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParkListResponseTest {

    @Test
    public void testParkListResponse() {
        List<SearchParkDto> expectedParks = new ArrayList<>();
        expectedParks.add(new SearchParkDto());
        expectedParks.add(new SearchParkDto());

        ParkListResponse response = new ParkListResponse(expectedParks);
        response.setData(response.getData());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getParks());
        assertEquals(2, response.getData().getParks().size(), "There should be two parks in the list.");
        assertSame(expectedParks, response.getData().getParks(), "The parks list should be the same as what was passed to the constructor.");
    }
}