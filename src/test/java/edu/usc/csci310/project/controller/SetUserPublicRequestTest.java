package edu.usc.csci310.project.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetUserPublicRequestTest {

    @Test
    void testGetAndSetIsPublic() {
        SetUserPublicRequest request = new SetUserPublicRequest();

        assertNull(request.getIsPublic(), "isPublic should initially be null");

        request.setIsPublic(true);
        request.setUserId(1L);
        assertTrue(request.getIsPublic(), "isPublic should return true after being set to true");
        assertEquals(1L, request.getUserId());

        request.setIsPublic(false);
        assertFalse(request.getIsPublic(), "isPublic should return false after being set to false");

        request.setIsPublic(null);
        assertNull(request.getIsPublic(), "isPublic should be able to be set to null");
    }

    @Test
    void testConstructor() {
        SetUserPublicRequest trueRequest = new SetUserPublicRequest(true);
        assertTrue(trueRequest.getIsPublic(), "Constructed with true should return true");

        SetUserPublicRequest falseRequest = new SetUserPublicRequest(false);
        assertFalse(falseRequest.getIsPublic(), "Constructed with false should return false");

        SetUserPublicRequest nullRequest = new SetUserPublicRequest(null);
        assertNull(nullRequest.getIsPublic(), "Constructed with null should return null");
    }
}