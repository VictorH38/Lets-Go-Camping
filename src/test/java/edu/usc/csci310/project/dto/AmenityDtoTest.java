package edu.usc.csci310.project.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmenityDtoTest {
    @Test
    public void testAmenityDtoProperties() {
        Long expectedId = 42L;
        String expectedName = "Swimming Pool";

        AmenityDto amenityDto = new AmenityDto(expectedId, expectedName);

        assertEquals(expectedId, amenityDto.getId(), "The ID should be correctly set and retrieved.");
        assertEquals(expectedName, amenityDto.getName(), "The name should be correctly set and retrieved.");
    }

}