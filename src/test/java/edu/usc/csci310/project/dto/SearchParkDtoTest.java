package edu.usc.csci310.project.dto;

import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.domain.ParkImage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchParkDtoTest {
    @Test
    public void testSearchParkDtoCreationAndBehavior() {
        Park park = new Park();
        Boolean isFavorite = true;
        List<AmenityDto> amenities = List.of(new AmenityDto(1L, "Picnic Area"));
        List<ActivityDto> activities = List.of(new ActivityDto(1L, "Hiking"));
        List<ParkImage> parkImages = List.of(new ParkImage());

        SearchParkDto searchParkDto = new SearchParkDto(park, isFavorite, amenities, activities, parkImages);

        assertSame(park, searchParkDto.getPark(), "Park should match the constructor argument.");
        assertEquals(isFavorite, searchParkDto.getIsFavorite(), "IsFavorite should match the constructor argument.");
        assertSame(amenities, searchParkDto.getAmenities(), "Amenities list should match the constructor argument.");
        assertSame(activities, searchParkDto.getActivities(), "Activities list should match the constructor argument.");
        assertSame(parkImages, searchParkDto.getParkImages(), "Park images list should match the constructor argument.");

        Park newPark = new Park();
        searchParkDto.setPark(newPark);
        searchParkDto.setIsFavorite(false);
        List<AmenityDto> newAmenities = List.of(new AmenityDto(2L, "Water Park"));
        searchParkDto.setAmenities(newAmenities);
        List<ActivityDto> newActivities = List.of(new ActivityDto(2L, "Swimming"));
        searchParkDto.setActivities(newActivities);
        List<ParkImage> newParkImages = List.of(new ParkImage());
        searchParkDto.setParkImages(newParkImages);
        new SearchParkDto();
        SearchParkDto.from(newPark, true, newAmenities, newActivities, newParkImages);

        assertSame(newPark, searchParkDto.getPark(), "Park should be updated to new value.");
        assertFalse(searchParkDto.getIsFavorite(), "IsFavorite should be updated to false.");
        assertSame(newAmenities, searchParkDto.getAmenities(), "Amenities list should be updated to new value.");
        assertSame(newActivities, searchParkDto.getActivities(), "Activities list should be updated to new value.");
        assertSame(newParkImages, searchParkDto.getParkImages(), "Park images list should be updated to new value.");
    }

}