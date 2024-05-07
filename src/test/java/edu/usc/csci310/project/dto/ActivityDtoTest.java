package edu.usc.csci310.project.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDtoTest {
    @Test
    public void testActivityDto() {
        Long expectedId = 1L;
        String expectedName = "Hiking";

        ActivityDto activityDto = new ActivityDto(expectedId, expectedName);

        assertEquals(expectedId, activityDto.getId(), "The ID should match the one provided at construction.");
        assertEquals(expectedName, activityDto.getName(), "The name should match the one provided at construction.");
    }

}