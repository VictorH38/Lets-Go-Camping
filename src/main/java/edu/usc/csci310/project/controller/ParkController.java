package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Amenity;
import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.dto.FavoriteParkDto;
import edu.usc.csci310.project.dto.SearchParkDto;
import edu.usc.csci310.project.dto.StateParksDto;
import edu.usc.csci310.project.service.ParkService;
import edu.usc.csci310.project.util.ParkNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parks")
public class ParkController {

    @Autowired
    private ParkService parkService;

    @GetMapping("/search")
    public ParkListResponse searchParks(@RequestParam String searchName) {
        List<String> searchParams = Arrays.asList(searchName.split("\\+"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<SearchParkDto> parks =  parkService.searchParks(searchParams, authentication.getName());
        return new ParkListResponse(parks);
    }

    @GetMapping("/{parkId}")
    public ResponseEntity<GetParkResponse> getPark(@PathVariable Long parkId) {
        Park park = parkService.getParkById(parkId);
        if (park != null) {
            return ResponseEntity.ok(new GetParkResponse(park));
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Park data not found", new ParkNotFoundException("No data available for park ID: " + parkId));
        }
    }

    @PostMapping("/favorite")
    public void createFavoritePark(@RequestBody CreateFavoriteParkRequest req) {
        parkService.createFavoritePark(req.getUserId(), req.getParkId());
    }

    @PostMapping("/unfavorite")
    public void removeFavoritePark(@RequestBody CreateFavoriteParkRequest req) {
        parkService.removeFavoritePark(req.getUserId(), req.getParkId());
    }

    @GetMapping("/favorite/{userId}")
    public FavoriteParkListResponse getFavoriteParks(@PathVariable Long userId) {
        List<FavoriteParkDto> parks = parkService.getFavoriteParks(userId);
        return new FavoriteParkListResponse(parks);
    }

    @GetMapping("/activities")
    public GetParkActivitiesResponse getActivities() {
        return new GetParkActivitiesResponse(parkService.getAllActivities());
    }

    @GetMapping("/states")
    public GetStateParksResponse getStates() {
        var parksGroupedByState = parkService.getParksGroupedByState();
        var stateParksDTOs = parksGroupedByState.entrySet().stream()
                .map(entry -> new StateParksDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return new GetStateParksResponse(stateParksDTOs);
    }

    @PostMapping("/favorite/ranks")
    public ResponseEntity<?> updateParkRanks(@RequestBody UpdateFavoriteParksRanksRequest request) {
        parkService.updateFavoriteParkRanks(request.getUserId(), request.getParkIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/amenities")
    public GetAmenitiesParksResponse getAllAmenitiesWithParks() {
        List<Amenity> amenities = parkService.getAmenities();
        return new GetAmenitiesParksResponse(amenities);
    }
}
