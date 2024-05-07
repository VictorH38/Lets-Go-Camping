package edu.usc.csci310.project.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String name;

    @ManyToMany(mappedBy = "activities")
    private Set<Park> parks = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Park> getParks() {
        if (this.parks == null) {
            this.parks = new HashSet<>();
        }

        return parks;
    }

    public void setParks(Set<Park> parks) {
        this.parks = parks;
    }

    public void addPark(Park park) {
        this.parks.add(park);
        park.getActivities().add(this);
    }

    public Activity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Activity(String name) {
        this.name = name;
    }

    public Activity() {}

}
