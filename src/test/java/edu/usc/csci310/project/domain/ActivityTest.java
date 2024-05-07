package edu.usc.csci310.project.domain;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTest {

    @Test
    void testGetterAndSetter() {
        Activity activity = new Activity();
        activity.setId(3L);
        activity.setName("Scuba Diving");

        Park park1 = new Park();
        park1.setId(2L);

        Park park2 = new Park();
        park2.setId(4L);

        Set<Park> parks = new HashSet<>();
        parks.add(park1);
        parks.add(park2);

        activity.setParks(parks);

        assertEquals(3L, activity.getId());
        assertEquals("Scuba Diving", activity.getName());

        Set<Park> retrievedParks = activity.getParks();
        assertTrue(retrievedParks.contains(park1));
        assertTrue(retrievedParks.contains(park2));
        assertEquals(2, retrievedParks.size());
    }

    @Test
    void testGetParksWhenNull() throws NoSuchFieldException, IllegalAccessException {
        Activity activity = new Activity();

        Field parksField = Activity.class.getDeclaredField("parks");
        parksField.setAccessible(true);
        parksField.set(activity, null);

        Set<Park> parks = activity.getParks();
        assertNotNull(parks);
        assertTrue(parks.isEmpty());
    }

    @Test
    void testConstructor() {
        Activity activity = new Activity(1L, "Underwater Basket Weaving");
        assertEquals(1L, activity.getId());
        assertEquals("Underwater Basket Weaving", activity.getName());
    }

    @Test
    void testConstructorWithName() {
        Activity activity = new Activity("Hiking");
        assertNull(activity.getId());
        assertEquals("Hiking", activity.getName());
    }

    @Test
    void testAddPark() {
        Activity activity = new Activity();
        activity.setName("Biking");

        Park park = new Park();
        park.setId(1L);

        activity.addPark(park);

        assertTrue(activity.getParks().contains(park));
        assertTrue(park.getActivities().contains(activity));
    }
}