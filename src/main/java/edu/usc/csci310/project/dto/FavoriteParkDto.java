package edu.usc.csci310.project.dto;

import edu.usc.csci310.project.domain.Park;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FavoriteParkDto {
    private Park park;
    private int rank;

    public FavoriteParkDto() {}

    public FavoriteParkDto(Park park, int rank) {
        this.park = park;
        this.rank = rank;
    }

    public static List<FavoriteParkDto> from(Map<Integer, Park> rankToParkMap) {
        return rankToParkMap.entrySet().stream()
                .map(entry -> new FavoriteParkDto(entry.getValue(), entry.getKey()))
                .sorted(Comparator.comparingInt(FavoriteParkDto::getRank))
                .collect(Collectors.toList());
    }

    public Park getPark() {
        return park;
    }

    public void setPark(Park park) {
        this.park = park;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}

