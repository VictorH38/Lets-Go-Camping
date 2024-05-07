package edu.usc.csci310.project.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OperatingHoursTest {

    @Test
    void testGetterAndSetter() {
        OperatingHours operatingHours = new OperatingHours();
        operatingHours.setId(1L);
        operatingHours.setDescription("Park is open year-round. Check website for facility hours.");
        operatingHours.setName("Acadia National Park");
        operatingHours.setMonday("All Day");
        operatingHours.setTuesday("All Day");
        operatingHours.setWednesday("All Day");
        operatingHours.setThursday("All Day");
        operatingHours.setFriday("All Day");
        operatingHours.setSaturday("All Day");
        operatingHours.setSunday("All Day");

        Park park = new Park();
        park.setId(2L);
        operatingHours.setPark(park);

        assertEquals(1L, operatingHours.getId());
        assertEquals("Park is open year-round. Check website for facility hours.", operatingHours.getDescription());
        assertEquals("Acadia National Park", operatingHours.getName());
        assertEquals("All Day", operatingHours.getMonday());
        assertEquals("All Day", operatingHours.getTuesday());
        assertEquals("All Day", operatingHours.getWednesday());
        assertEquals("All Day", operatingHours.getThursday());
        assertEquals("All Day", operatingHours.getFriday());
        assertEquals("All Day", operatingHours.getSaturday());
        assertEquals("All Day", operatingHours.getSunday());

        assertNotNull(operatingHours.getPark());
        assertEquals(2L, operatingHours.getPark().getId());
    }

    @Test
    void testConstructor() {
        OperatingHours operatingHours = new OperatingHours(2L, "Park has seasonal hours. Visit website for more information.", "Yellowstone National Park");

        assertEquals(2L, operatingHours.getId());
        assertEquals("Park has seasonal hours. Visit website for more information.", operatingHours.getDescription());
        assertEquals("Yellowstone National Park", operatingHours.getName());
    }
}
