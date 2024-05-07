package edu.usc.csci310.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Park {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String shortName;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String fullName;

    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String description;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String weather;
    private String designation;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String directionsInfo;
    private String directionsURL;
    private String states;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String address;
    private String latitude;
    private String longitude;

    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
            name = "park_amenity",
            joinColumns = @JoinColumn(name = "park_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "park")
    private List<EntranceFee> fees = new ArrayList<>();
    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(
            name = "park_activity",
            joinColumns = @JoinColumn(name = "park_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<Activity> activities = new HashSet<>();
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "park")
    private List<OperatingHours> operatingHours = new ArrayList<>();
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "park")
    private List<ParkImage> parkImages = new ArrayList<>();

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getDirectionsURL() {
        return directionsURL;
    }

    public void setDirectionsURL(String directionsURL) {
        this.directionsURL = directionsURL;
    }

    public String getDirectionsInfo() {
        return directionsInfo;
    }

    public void setDirectionsInfo(String directionsInfo) {
        this.directionsInfo = directionsInfo;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(Set<Amenity> amenities) {
        this.amenities = amenities;
    }

    public List<ParkImage> getParkImages() {
        return parkImages;
    }

    public void setParkImages(List<ParkImage> parkImages) {
        this.parkImages = parkImages;
    }

    public List<OperatingHours> getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(List<OperatingHours> operatingHours) {
        this.operatingHours = operatingHours;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public List<EntranceFee> getFees() {
        return fees;
    }

    public void setFees(List<EntranceFee> fees) {
        this.fees = fees;
    }

    public void addAmenity(Amenity amenity) {
        this.amenities.add(amenity);
        amenity.getParks().add(this);
    }

    public void addActivity(Activity activity) {
        this.activities.add(activity);
        activity.getParks().add(this);
    }

    public void addEntranceFee(EntranceFee entranceFee) {
        fees.add(entranceFee);
        entranceFee.setPark(this);
    }

    public void addOperatingHours(OperatingHours operatingHour) {
        operatingHours.add(operatingHour);
        operatingHour.setPark(this);
    }

    public void addParkImage(ParkImage parkImage) {
        parkImages.add(parkImage);
        parkImage.setPark(this);
    }

    public Park() {}

    public Park(Long id, String shortName, String fullName, String description, String weather, String designation, String states, String latitude, String longitude, String address, String directionsInfo, String directionsURL, Set<Amenity> amenities, Set<Activity> activities, List<EntranceFee> fees, List<OperatingHours> operatingHours, List<ParkImage> parkImages) {
       this.id = id;
       this.shortName = shortName;
       this.fullName = fullName;
       this.description = description;
       this.weather = weather;
       this.designation = designation;
       this.states = states;
       this.latitude = latitude;
       this.longitude = longitude;
       this.address = address;
       this.directionsInfo = directionsInfo;
       this.directionsURL = directionsURL;
       this.amenities = amenities;
       this.activities = activities;
       this.fees = fees;
       this.operatingHours = operatingHours;
       this.parkImages = parkImages;
    }
}
