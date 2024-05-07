package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Amenity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetAmenitiesParksResponseTest {
    @Test
    void testGetAndSetData() {
        Amenity amenity1 = new Amenity();
        amenity1.setName("Picnic Area");
        Amenity amenity2 = new Amenity();
        amenity2.setName("Hiking Trail");

        List<Amenity> amenities = Arrays.asList(amenity1, amenity2);

        GetAmenitiesParksResponse response = new GetAmenitiesParksResponse(amenities);

        assertNotNull(response.getData(), "Data should not be null");
        assertEquals(2, response.getData().size(), "Data should contain two amenities");
        assertEquals("Picnic Area", response.getData().get(0).getName(), "The first amenity should be 'Picnic Area'");
        assertEquals("Hiking Trail", response.getData().get(1).getName(), "The second amenity should be 'Hiking Trail'");

        Amenity amenity3 = new Amenity();
        amenity3.setName("Biking Trail");
        List<Amenity> newAmenities = new ArrayList<>();
        newAmenities.add(amenity3);

        response.setData(newAmenities);

        assertNotNull(response.getData(), "Data should not be null after setting new list");
        assertEquals(1, response.getData().size(), "Data should contain one amenity after setting new list");
        assertEquals("Biking Trail", response.getData().get(0).getName(), "The amenity should be 'Biking Trail' after setting new list");
    }

}