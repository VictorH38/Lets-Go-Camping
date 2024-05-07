package edu.usc.csci310.project.dto;

public class AmenityDto {
    private Long id;
    private String name;

    public AmenityDto(Long id, String name) {
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
