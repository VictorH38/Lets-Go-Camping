package edu.usc.csci310.project.dto;

import edu.usc.csci310.project.domain.Park;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StateParksDtoTest {

    @Test
    public void testConstructorAndGetter() {
        Park park1 = new Park();
        park1.setId(1L);
        park1.setFullName("Yellowstone");
        Park park2 = new Park();
        park2.setId(2L);
        park2.setFullName("Yosemite");

        List<Park> parks = new ArrayList<>();
        parks.add(park1);
        parks.add(park2);
        String state = "WY";

        StateParksDto dto = new StateParksDto(state, parks);

        assertEquals(state, dto.getState());
        assertNotNull(dto.getParks());
        assertEquals(2, dto.getParks().size());
        assertEquals(park1, dto.getParks().get(0));
        assertEquals(park2, dto.getParks().get(1));
    }

    @Test
    public void testSetters() {
        StateParksDto dto = new StateParksDto("CA", new ArrayList<>());

        String newState = "NV";
        List<Park> newParks = new ArrayList<>();
        Park newPark = new Park();
        newPark.setId(3L);
        newPark.setFullName("Great Basin");
        newParks.add(newPark);

        dto.setState(newState);
        dto.setParks(newParks);

        assertEquals(newState, dto.getState());
        assertNotNull(dto.getParks());
        assertEquals(1, dto.getParks().size());
        assertEquals(newPark, dto.getParks().get(0));
    }
}
