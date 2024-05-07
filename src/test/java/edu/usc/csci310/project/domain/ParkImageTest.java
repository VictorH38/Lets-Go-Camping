package edu.usc.csci310.project.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParkImageTest {

    @Test
    void testGetterAndSetter() {
        ParkImage parkImage = new ParkImage();
        parkImage.setId(1L);
        parkImage.setTitle("Sunset View");
        parkImage.setCaption("A stunning sunset view from the ridge.");
        parkImage.setUrl("https://example.com/sunset.jpg");
        parkImage.setAltText("Sunset at the park");

        Park park = new Park();
        park.setId(2L);
        parkImage.setPark(park);

        assertEquals(1L, parkImage.getId());
        assertEquals("Sunset View", parkImage.getTitle());
        assertEquals("A stunning sunset view from the ridge.", parkImage.getCaption());
        assertEquals("https://example.com/sunset.jpg", parkImage.getUrl());
        assertEquals("Sunset at the park", parkImage.getAltText());

        assertNotNull(parkImage.getPark());
        assertEquals(2L, parkImage.getPark().getId());
    }

    @Test
    void testConstructor() {
        ParkImage parkImage = new ParkImage(2L, "Mountain Peak", "The highest peak in the park.", "https://example.com/mountain.jpg", "Mountain peak covered in snow");

        assertEquals(2L, parkImage.getId());
        assertEquals("Mountain Peak", parkImage.getTitle());
        assertEquals("The highest peak in the park.", parkImage.getCaption());
        assertEquals("https://example.com/mountain.jpg", parkImage.getUrl());
        assertEquals("Mountain peak covered in snow", parkImage.getAltText());
    }
}
