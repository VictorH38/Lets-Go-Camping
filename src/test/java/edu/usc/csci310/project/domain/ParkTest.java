package edu.usc.csci310.project.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ParkTest {

    @Test
    void testGetterAndSetter() {
        Park park = new Park();
        park.setId(2L);
        park.setDescription("Test Park for Testing Purposes");
        park.setShortName("Matthew's Park");
        park.setFullName("Matthew's National Forest and Conservation Area");
        park.setWeather("It is always sunny");
        park.setDesignation("National Forest");
        park.setDirectionsInfo("Drive to the park");
        park.setDirectionsURL("https://google.com");
        park.setAddress("123 Park St, CA 98322");
        park.setStates("MD,VA");
        park.setLatitude("44.59824417");
        park.setLongitude("-110.5471695");
        park.setAmenities(Set.of(new Amenity(1L, "ATM/Cash Machine")));
        park.setActivities(Set.of(new Activity(1L, "Underwater Basket Weaving")));
        park.setFees(List.of(new EntranceFee(2L, "Crap Fee", "Crap Fee Description", 500)));
        park.setOperatingHours(List.of(new OperatingHours(1L, "Matthew's Park", "Matthew's Park is always open")));
        park.setParkImages(List.of(new ParkImage(1L, "Grant Lake", "Grant Lake is home to many animals", "https://google.com", "A lake with birds flying above and ducks in the water")));
        assertEquals(2L, park.getId());
        assertEquals("Test Park for Testing Purposes", park.getDescription());
        assertEquals("Matthew's Park", park.getShortName());
        assertEquals("Matthew's National Forest and Conservation Area", park.getFullName());
        assertEquals("It is always sunny", park.getWeather());
        assertEquals("National Forest", park.getDesignation());
        assertEquals("Drive to the park", park.getDirectionsInfo());
        assertEquals("https://google.com", park.getDirectionsURL());
        assertEquals("123 Park St, CA 98322", park.getAddress());
        assertEquals("MD,VA", park.getStates());
        assertEquals("44.59824417", park.getLatitude());
        assertEquals("-110.5471695", park.getLongitude());
        assertEquals(1, park.getAmenities().size());
        assertEquals(1, park.getActivities().size());
        assertEquals(1, park.getFees().size());
        assertEquals(1, park.getOperatingHours().size());
        assertEquals(1, park.getParkImages().size());
    }

    @Test
    void testConstructor() {
        Set<Amenity> amenityList = Set.of(new Amenity(1L, "ATM/Cash Machine"));
        Set<Activity> activityList = Set.of(new Activity(1L, "Underwater Basket Weaving"));
        List<EntranceFee> feeList = List.of(new EntranceFee(2L, "Crap Fee", "Crap Fee Description", 500));
        List<OperatingHours> operatingHoursList = List.of(new OperatingHours(1L, "Matthew's Park", "Matthew's Park is always open"));
        List<ParkImage> parkImageList = List.of(new ParkImage(1L, "Grant Lake", "Grant Lake is home to many animals", "https://google.com", "A lake with birds flying above and ducks in the water"));
        Park p = new Park(
            1L,
            "Matthew's Park",
            "Matthew's National Forest and Conservation Area",
            "Test Park for Testing Purposes",
            "It is always sunny",
            "National Forest",
            "MD, VA",
            "44.59824417",
            "-110.5471695",
            "123 Park St, CA 98322",
            "Drive to the park",
            "https://google.com",
            amenityList,
            activityList,
            feeList,
            operatingHoursList,
            parkImageList
        );
        assertEquals(1L, p.getId());
    }

    @Test
    void testAddAmenity() {
        Park park = new Park();
        Amenity amenity = new Amenity();

        park.addAmenity(amenity);

        assertEquals(1, park.getAmenities().size());
        assertTrue(amenity.getParks().contains(park));

        assertEquals(1, amenity.getParks().size());
    }

    @Test
    void testAddActivity() {
        Park park = new Park();
        Activity activity = new Activity();

        park.addActivity(activity);

        assertEquals(1, park.getActivities().size());
        assertTrue(activity.getParks().contains(park));

        assertEquals(1, activity.getParks().size());
    }

    @Test
    void testAddEntranceFee() {
        Park park = new Park();
        EntranceFee entranceFee = new EntranceFee();
        park.addEntranceFee(entranceFee);

        assertEquals(1, park.getFees().size());
        assertSame(park, park.getFees().get(0).getPark());
    }

    @Test
    void testAddOperatingHours() {
        Park park = new Park();
        OperatingHours operatingHours = new OperatingHours();
        park.addOperatingHours(operatingHours);

        assertEquals(1, park.getOperatingHours().size());
        assertSame(park, park.getOperatingHours().get(0).getPark());
    }

    @Test
    void testAddParkImage() {
        Park park = new Park();
        ParkImage parkImage = new ParkImage();
        park.addParkImage(parkImage);

        assertEquals(1, park.getParkImages().size());
        assertSame(park, park.getParkImages().get(0).getPark());
    }
}
