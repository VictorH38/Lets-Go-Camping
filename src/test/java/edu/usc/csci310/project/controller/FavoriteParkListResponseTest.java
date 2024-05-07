package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.domain.User;
import edu.usc.csci310.project.dto.FavoriteParkDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;

public class FavoriteParkListResponseTest {

    private FavoriteParkListResponse favoriteParkListResponse;
    private List<FavoriteParkDto> favoriteParkDtos;


    @Test
    public void testGetParks() {
        User user = new User(1L, "name", "a@a.com", "123");
        Park park1 = new Park();
        park1.setId(1L);
        Park park2 = new Park();
        park2.setId(2L);
        favoriteParkDtos = new ArrayList<>();
        FavoriteParkDto fp1 = new FavoriteParkDto();
        fp1.setPark(park1);
        FavoriteParkDto fp2 = new FavoriteParkDto();
        fp2.setPark(park2);
        favoriteParkDtos.add(fp1);
        favoriteParkDtos.add(fp2);

        favoriteParkListResponse = new FavoriteParkListResponse(favoriteParkDtos);
        favoriteParkListResponse.setData(favoriteParkListResponse.getData());
        List<FavoriteParkDto> retrievedParks = favoriteParkListResponse.getData().getParks();
        assertEquals("Expected to retrieve the same list of parks that was set", favoriteParkDtos, retrievedParks);
    }
}
