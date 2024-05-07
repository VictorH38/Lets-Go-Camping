package edu.usc.csci310.project.domain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;


public class FavoriteParkTest {

    @Test
    public void testSetAndGetUser() {
        User mockUser = Mockito.mock(User.class);

        FavoritePark favoritePark = new FavoritePark();
        favoritePark.setUser(mockUser);

        assertEquals(mockUser, favoritePark.getUser(), "The returned user should match the one that was set");
    }

    @Test
    public void testSetAndGetPark() {
        Park mockPark = Mockito.mock(Park.class);

        FavoritePark favoritePark = new FavoritePark();
        favoritePark.setPark(mockPark);

        assertEquals(mockPark, favoritePark.getPark(), "The returned park should match the one that was set");
    }
}