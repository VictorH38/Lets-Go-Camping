package edu.usc.csci310.project.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.usc.csci310.project.domain.Park;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteParkDtoTest {

    @Test
    public void testConstructorsAndSetters() {
        Park park = new Park();
        FavoriteParkDto dto = new FavoriteParkDto(park, 1);
        FavoriteParkDto dto2 = new FavoriteParkDto();
        assertEquals(park, dto.getPark());
        assertEquals(1, dto.getRank());

        Park newPark = new Park();
        dto.setPark(newPark);
        dto.setRank(2);
        assertEquals(newPark, dto.getPark());
        assertEquals(2, dto.getRank());
    }

    @Test
    public void testFromMethod() {
        Map<Integer, Park> rankToParkMap = new HashMap<>();
        rankToParkMap.put(3, new Park());
        rankToParkMap.put(1, new Park());
        rankToParkMap.put(2, new Park());

        List<FavoriteParkDto> dtos = FavoriteParkDto.from(rankToParkMap);

        assertEquals(3, dtos.size());
        assertEquals(1, dtos.get(0).getRank());
        assertEquals(2, dtos.get(1).getRank());
        assertEquals(3, dtos.get(2).getRank());
    }
}