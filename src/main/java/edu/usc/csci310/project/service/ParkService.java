package edu.usc.csci310.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usc.csci310.project.dto.ActivityDto;
import edu.usc.csci310.project.dto.AmenityDto;
import edu.usc.csci310.project.dto.FavoriteParkDto;
import edu.usc.csci310.project.repository.*;
import edu.usc.csci310.project.dto.SearchParkDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


import edu.usc.csci310.project.domain.*;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ParkService implements CommandLineRunner {

    @Autowired
    private ParkRepository parkRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ParkImageRepository parkImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteParkRepository favoriteParkRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @Transactional
    public void importParks() throws IOException {
        String jsonData;
        try (InputStream is = resourceLoader.getResource("classpath:parks.json").getInputStream()) {
            jsonData = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonData);
        Map<String, Activity> existingActivities = activityRepository.findAll().stream()
                .collect(Collectors.toMap(Activity::getName, Function.identity()));

        String amenityJsonData;
        try (InputStream amenityInputStream = resourceLoader.getResource("classpath:amenities.json").getInputStream()) {
            amenityJsonData = new String(amenityInputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        ObjectMapper amenityObjectMapper = new ObjectMapper();
        JsonNode amenityRootNode = amenityObjectMapper.readTree(amenityJsonData);
        List<Amenity> amenitiesList = new ArrayList<>();
        JsonNode amenityArray = amenityRootNode.path("data");
        if (amenityArray.isArray()) {
            for (JsonNode amenityNode : amenityArray) {
                Amenity amenity = new Amenity();
                amenity.setName(amenityNode.get("name").asText());
                amenitiesList.add(amenity);
            }
        }

        JsonNode dataArray = rootNode.path("data");
        if (dataArray.isArray()) {
            int counter = 1;
            for (JsonNode node : dataArray) {
                Park park = new Park();

                park.setShortName(node.get("name").asText());
                park.setFullName(node.get("fullName").asText());
                park.setDescription(node.get("description").asText());
                park.setWeather(node.get("weatherInfo").asText());
                park.setDesignation(node.get("designation").asText());
                park.setDirectionsInfo(node.get("directionsInfo").asText());
                park.setDirectionsURL(node.get("directionsUrl").asText());
                park.setStates(node.get("states").asText());
                park.setLatitude(node.get("latitude").asText());
                park.setLongitude(node.get("longitude").asText());

                if (node.has("addresses") && node.get("addresses").isArray()) {
                    for (JsonNode addressNode : node.get("addresses")) {
                        if ("Physical".equals(addressNode.get("type").asText(null))) {
                            String address = addressNode.get("line1").asText("");
                            String stateCode = addressNode.get("stateCode").asText("");
                            String postalCode = addressNode.get("postalCode").asText("");

                            String fullAddress = address;
                            if (!stateCode.isEmpty()) {
                                fullAddress += ", " + stateCode;
                            }
                            if (!postalCode.isEmpty()) {
                                fullAddress += " " + postalCode;
                            }

                            park.setAddress(fullAddress);
                            break;
                        }
                    }
                }

                if (node.has("activities") && node.get("activities").isArray()) {
                    for (JsonNode activityNode : node.get("activities")) {
                        String activityName = activityNode.get("name").asText();
                        Activity activity = existingActivities.computeIfAbsent(activityName, name -> {
                            Activity newActivity = new Activity();
                            newActivity.setName(name);
                            return newActivity;
                        });
                        park.getActivities().add(activity);
                        activity.getParks().add(park);
                    }
                }

                if (node.has("entranceFees") && node.get("entranceFees").isArray()) {
                    for (JsonNode feeNode : node.get("entranceFees")) {
                        EntranceFee entranceFee = new EntranceFee();
                        entranceFee.setDescription(feeNode.get("description").asText());
                        entranceFee.setTitle(feeNode.get("title").asText());

                        try {
                            double cost = Double.parseDouble(feeNode.get("cost").asText());
                            entranceFee.setCost(cost);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing cost for entrance fee: " + e.getMessage());
                            entranceFee.setCost(0.0f);
                        }

                        park.addEntranceFee(entranceFee);
                    }
                }

                if (node.has("operatingHours") && node.get("operatingHours").isArray()) {
                    for (JsonNode operatingHourNode : node.get("operatingHours")) {
                        OperatingHours operatingHours = new OperatingHours();
                        operatingHours.setDescription(operatingHourNode.get("description").asText());
                        operatingHours.setName(operatingHourNode.get("name").asText());

                        JsonNode standardHoursNode = operatingHourNode.get("standardHours");
                        if (standardHoursNode != null) {
                            operatingHours.setMonday(standardHoursNode.get("monday").asText("Closed"));
                            operatingHours.setTuesday(standardHoursNode.get("tuesday").asText("Closed"));
                            operatingHours.setWednesday(standardHoursNode.get("wednesday").asText("Closed"));
                            operatingHours.setThursday(standardHoursNode.get("thursday").asText("Closed"));
                            operatingHours.setFriday(standardHoursNode.get("friday").asText("Closed"));
                            operatingHours.setSaturday(standardHoursNode.get("saturday").asText("Closed"));
                            operatingHours.setSunday(standardHoursNode.get("sunday").asText("Closed"));
                        }

                        park.addOperatingHours(operatingHours);
                    }
                }

                if (node.has("images") && node.get("images").isArray()) {
                    for (JsonNode imageNode : node.get("images")) {
                        ParkImage image = new ParkImage();
                        image.setTitle(imageNode.get("title").asText());
                        image.setCaption(imageNode.get("caption").asText());
                        image.setUrl(imageNode.get("url").asText());
                        image.setAltText(imageNode.get("altText").asText());

                        park.addParkImage(image);
                    }
                }

                if (amenitiesList.size() >= 3) {
                    Set<Amenity> chosenAmenities = new HashSet<>();
                    chosenAmenities.add(amenitiesList.get(counter % 15));
                    chosenAmenities.add(amenitiesList.get((counter + 1) % 15));
                    chosenAmenities.add(amenitiesList.get((counter + 2) % 15));

                    for (Amenity amenity : chosenAmenities) {
                        park.addAmenity(amenity);
                    }
                }

                parkRepository.save(park);
                counter += 1;
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        if (parkRepository.count() == 0) {
            executeImportWithTransaction();
        }
    }

    protected void executeImportWithTransaction() {
        transactionTemplate.execute(status -> {
            try {
                importParks();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public List<SearchParkDto> searchParks(List<String> keywords, String email) {
        String searchWords = String.join(" ", keywords);
        List<Park> parks = parkRepository.findByNameContaining(searchWords);
        User user = userRepository.findByEmail(email);

        Set<Long> favoriteParkIds = favoriteParkRepository.findAllByUserIdOrderByRankAsc(user.getId()).stream()
                .map(FavoritePark::getPark)
                .map(Park::getId)
                .collect(Collectors.toSet());

        List<SearchParkDto> res = new ArrayList<>();
        for (Park park : parks) {
            Long parkId = park.getId();
            List<ParkImage> parkImages = parkImageRepository.findByParkId(parkId);
            List<ActivityDto> activities = activityRepository.findAllActivityDto(parkId);
            List<AmenityDto> amenities = amenityRepository.findAllAmenities(parkId);
            res.add(SearchParkDto.from(park, favoriteParkIds.contains(parkId), amenities, activities, parkImages));
        }

        return res;
    }

    public Park getParkById(Long id) {
        Optional<Park> park = parkRepository.findById(id);
        return park.orElse(null);
    }

    public void createFavoritePark(Long userId, Long parkId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Park> park = parkRepository.findById(parkId);

        if (user.isEmpty() || park.isEmpty()) {
            return;
        }

        Optional<FavoritePark> existingFavoritePark = favoriteParkRepository.findByUserIdAndParkId(userId, parkId);

        if (existingFavoritePark.isPresent()) {
            return;
        }

        int rank = 1;
        List<FavoritePark> favoriteParks = favoriteParkRepository.findAllByUserIdOrderByRankAsc(userId);
        if (!favoriteParks.isEmpty()) {
            rank = favoriteParks.get(favoriteParks.size()-1).getRank() + 1;
        }

        FavoritePark favoritePark = new FavoritePark();
        favoritePark.setUser(user.get());
        favoritePark.setPark(park.get());
        favoritePark.setRank(rank);
        favoriteParkRepository.save(favoritePark);
    }

    public void removeFavoritePark(Long userId, Long parkId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Park> park = parkRepository.findById(parkId);

        if (user.isEmpty() || park.isEmpty()) {
            return;
        }

        Optional<FavoritePark> favoritePark = favoriteParkRepository.findByUserIdAndParkId(userId, parkId);
        if (favoritePark.isPresent()) {
            favoriteParkRepository.delete(favoritePark.get());

            List<FavoritePark> remainingFavoriteParks = favoriteParkRepository.findAllByUserIdOrderByRankAsc(userId);

            for (int i = 0; i < remainingFavoriteParks.size(); i++) {
                FavoritePark fp = remainingFavoriteParks.get(i);
                fp.setRank(i + 1);
                favoriteParkRepository.save(fp);
            }
        }
    }

    public List<FavoriteParkDto> getFavoriteParks(Long userId) {
        List<FavoritePark> favoriteParks = favoriteParkRepository.findAllByUserIdOrderByRankAsc(userId);
        List<Long> parkIds = new ArrayList<>();
        Map<Long, FavoritePark> favoriteParkMap = new HashMap<>();

        Map<Integer, Park> rankToPark = new HashMap<>();
        for (FavoritePark fp : favoriteParks) {
            parkIds.add(fp.getPark().getId());
            favoriteParkMap.put(fp.getPark().getId(), fp);
            rankToPark.put(fp.getRank(), null);
        }

        List<Park> parks = parkRepository.findAllById(parkIds);

        for (Park park : parks) {
            rankToPark.put(favoriteParkMap.get(park.getId()).getRank(), park);

        }
        return FavoriteParkDto.from(rankToPark);
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Map<String, List<Park>> getParksGroupedByState() {
        List<Park> parks = parkRepository.findAll();
        Map<String, List<Park>> parksByState = new HashMap<>();

        for (Park park : parks) {
            Arrays.stream(park.getStates().split(",")).forEach(state -> {
                parksByState.computeIfAbsent(state, k -> new ArrayList<>()).add(park);
            });
        }

        return parksByState;
    }

    public void updateFavoriteParkRanks(Long userId, List<Long> parkIds) {
        List<FavoritePark> favoriteParks = favoriteParkRepository.findAllByUserIdOrderByRankAsc(userId);
        if (favoriteParks.size() == parkIds.size()) {
            Map<Long, FavoritePark> parkIdToFavoriteMap = new HashMap<>();
            for (FavoritePark favoritePark : favoriteParks) {
                parkIdToFavoriteMap.put(favoritePark.getPark().getId(), favoritePark);
            }

            for (int i = 0; i < parkIds.size(); i++) {
                Long parkId = parkIds.get(i);
                FavoritePark favoritePark = parkIdToFavoriteMap.get(parkId);
                if (favoritePark != null) {
                    favoritePark.setRank(i + 1);
                }
            }

            favoriteParkRepository.saveAll(favoriteParks);
        }
    }

    public List<Amenity> getAmenities() {
        return amenityRepository.findAll();
    }
}
