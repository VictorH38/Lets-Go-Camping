package edu.usc.csci310.project.domain;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AmenityTest {

    @Test
    void testGetterAndSetter() {
        Amenity amenity = new Amenity();
        amenity.setId(3L);
        amenity.setName("ATM/Cash Machine");

        Park park1 = new Park();
        park1.setId(2L);

        Park park2 = new Park();
        park2.setId(4L);

        Set<Park> parks = new HashSet<>();
        parks.add(park1);
        parks.add(park2);

        amenity.setParks(parks);

        assertEquals(3L, amenity.getId());
        assertEquals("ATM/Cash Machine", amenity.getName());

        Set<Park> retrievedParks = amenity.getParks();
        assertTrue(retrievedParks.contains(park1));
        assertTrue(retrievedParks.contains(park2));
        assertEquals(2, retrievedParks.size());
    }

    @Test
    void testGetParksWhenNull() throws NoSuchFieldException, IllegalAccessException {
        Amenity amenity = new Amenity();

        Field parksField = Amenity.class.getDeclaredField("parks");
        parksField.setAccessible(true);
        parksField.set(amenity, null);

        Set<Park> parks = amenity.getParks();
        assertNotNull(parks);
        assertTrue(parks.isEmpty());
    }

    @Test
    void testConstructor() {
        Amenity amenity = new Amenity(1L, "ATM/Cash Machine");
        assertEquals(1L, amenity.getId());
        assertEquals("ATM/Cash Machine", amenity.getName());
    }

    @Test
    void testConstructorWithName() {
        Amenity amenity = new Amenity("ATM/Cash Machine");
        assertNull(amenity.getId());
        assertEquals("ATM/Cash Machine", amenity.getName());
    }

    @Test
    void testAddPark() {
        Amenity amenity = new Amenity();
        amenity.setName("ATM/Cash Machine");

        Park park = new Park();
        park.setId(1L);

        amenity.addPark(park);

        assertTrue(amenity.getParks().contains(park));
        assertTrue(park.getAmenities().contains(amenity));
    }
}
