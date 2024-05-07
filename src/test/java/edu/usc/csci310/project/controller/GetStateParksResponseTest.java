package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.dto.StateParksDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetStateParksResponseTest {
    @Test
    void testConstructorAndGetters() {
        StateParksDto park1 = new StateParksDto("Park 1", List.of());
        StateParksDto park2 = new StateParksDto("Park 2", List.of());
        List<StateParksDto> parks = Arrays.asList(park1, park2);

        GetStateParksResponse response = new GetStateParksResponse(parks);

        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals(park1, response.getData().get(0));
        assertEquals(park2, response.getData().get(1));
    }

    @Test
    void testSetData() {
        StateParksDto park1 = new StateParksDto("Park 1", List.of());
        StateParksDto park2 = new StateParksDto("Park 2", List.of());
        List<StateParksDto> parks = Arrays.asList(park1, park2);
        GetStateParksResponse response = new GetStateParksResponse(parks);

        StateParksDto park3 = new StateParksDto("Park 3", List.of());
        List<StateParksDto> newParks = Arrays.asList(park3);

        response.setData(newParks);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(park3, response.getData().get(0));
    }

}