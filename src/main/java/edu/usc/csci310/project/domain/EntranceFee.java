package edu.usc.csci310.project.domain;

import jakarta.persistence.*;

@Entity
public class EntranceFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String description;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String title;
    private double cost;

    @ManyToOne
    @JoinColumn(name = "park_id")
    private Park park;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Park getPark() {
        return park;
    }

    public void setPark(Park park) {
        this.park = park;
    }

    public EntranceFee() {}

    public EntranceFee(Long id,String description,String title,double cost) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.cost = cost;
    }
}
