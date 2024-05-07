package edu.usc.csci310.project.dto;

public class ActivityDto {
    private Long id;
    private String name;

    public ActivityDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}