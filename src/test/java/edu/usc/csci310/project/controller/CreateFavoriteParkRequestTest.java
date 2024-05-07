package edu.usc.csci310.project.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateFavoriteParkRequestTest {

    @Test
    public void testDefaultConstructor() {
        CreateFavoriteParkRequest request = new CreateFavoriteParkRequest();
        assertEquals(null, request.getUserId(), "Default userId should be null");
        assertEquals(null, request.getParkId(), "Default parkId should be null");
    }

    @Test
    public void testParameterizedConstructor() {
        Long userId = 1L;
        Long parkId = 2L;
        CreateFavoriteParkRequest request = new CreateFavoriteParkRequest(userId, parkId);

        assertEquals(userId, request.getUserId(), "userId should match the constructor argument");
        assertEquals(parkId, request.getParkId(), "parkId should match the constructor argument");
    }

    @Test
    public void testSetUserId() {
        Long userId = 3L;
        CreateFavoriteParkRequest request = new CreateFavoriteParkRequest();
        request.setUserId(userId);

        assertEquals(userId, request.getUserId(), "userId should be set correctly");
    }

    @Test
    public void testSetParkId() {
        Long parkId = 4L;
        CreateFavoriteParkRequest request = new CreateFavoriteParkRequest();
        request.setParkId(parkId);

        assertEquals(parkId, request.getParkId(), "parkId should be set correctly");
    }
}