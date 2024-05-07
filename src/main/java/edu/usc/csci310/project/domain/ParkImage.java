package edu.usc.csci310.project.domain;

import jakarta.persistence.*;

@Entity
public class ParkImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String title;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String caption;
    private String url;
    @Column(columnDefinition = "TEXT CHARSET utf8mb4")
    private String altText;

    @ManyToOne
    @JoinColumn(name = "park_id")
    private Park park;

    public ParkImage() {
    }

    public ParkImage(Long id, String title, String caption, String url, String altText) {
        this.id = id;
        this.title = title;
        this.caption = caption;
        this.url = url;
        this.altText = altText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Park getPark() {
        return park;
    }

    public void setPark(Park park) {
        this.park = park;
    }
}
