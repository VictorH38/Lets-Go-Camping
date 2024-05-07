package edu.usc.csci310.project.controller;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UpdateFavoriteParksRanksRequestTest {
    @Test
    void testGettersAndSetters() {
        UpdateFavoriteParksRanksRequest request = new UpdateFavoriteParksRanksRequest();
        request.setUserId(1L);
        request.setParkIds(Arrays.asList(1L, 2L, 3L));

        assertEquals(1L, request.getUserId());
        assertEquals(Arrays.asList(1L, 2L, 3L), request.getParkIds());
    }

    @Test
    void testConstructor() {
        UpdateFavoriteParksRanksRequest request = new UpdateFavoriteParksRanksRequest(1L, Arrays.asList(1L, 2L, 3L));

        assertEquals(1L, request.getUserId());
        assertEquals(Arrays.asList(1L, 2L, 3L), request.getParkIds());
    }

}