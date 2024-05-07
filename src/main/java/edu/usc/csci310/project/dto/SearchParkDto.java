package edu.usc.csci310.project.dto;

import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.domain.ParkImage;

import java.util.List;

public class SearchParkDto {
    private Park park;
    private Boolean isFavorite;
    private List<AmenityDto> amenities;
    private List<ActivityDto> activities;
    private List<ParkImage> parkImages;

    public SearchParkDto() {}

    public SearchParkDto(Park newPark, Boolean newIsFavorite,
                         List<AmenityDto> newAmenities, List<ActivityDto> newActivities,
                         List<ParkImage> newParkImages) {
        park = newPark;
        isFavorite = newIsFavorite;
        amenities = newAmenities;
        activities = newActivities;
        parkImages = newParkImages;
    }

    public static SearchParkDto from(Park newPark, Boolean newIsFavorite,
                            List<AmenityDto> newAmenities, List<ActivityDto> newActivities,
                            List<ParkImage> newParkImages) {
        return new SearchParkDto(newPark, newIsFavorite, newAmenities, newActivities, newParkImages);
    }

    public Park getPark() {
        return park;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public List<AmenityDto> getAmenities() {
        return amenities;
    }

    public List<ActivityDto> getActivities() {
        return activities;
    }

    public List<ParkImage> getParkImages() {
        return parkImages;
    }

    public void setPark(Park newPark) {
        park = newPark;
    }

    public void setIsFavorite(Boolean newIsFavorite) {
        isFavorite = newIsFavorite;
    }

    public void setAmenities(List<AmenityDto> newAmenities) {
        amenities = newAmenities;
    }

    public void setActivities(List<ActivityDto> newActivities) {
        activities = newActivities;
    }

    public void setParkImages(List<ParkImage> newParkImages) {
        parkImages = newParkImages;
    }
}
