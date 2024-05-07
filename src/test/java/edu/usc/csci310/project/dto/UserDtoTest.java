package edu.usc.csci310.project.dto;

import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.domain.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDtoTest {

    @Test
    void testGetterAndSetter() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setToken("token123");
        userDto.setIsPublic(true);

        assertEquals(1L, userDto.getId());
        assertEquals("Test User", userDto.getName());
        assertEquals("test@example.com", userDto.getEmail());
        assertEquals("token123", userDto.getToken());
        assertEquals(true, userDto.getIsPublic());
    }

    @Test
    void testConstructorAndFromMethod() {
        User user = new User("Test User", "test@example.com", "password");
        user.setId(1L);
        UserDto userDto = new UserDto(1L, "Test User", "test@example.com", "token123");
        UserDto userDtoFromMethod = UserDto.from(user, "token123");

        assertEquals(userDto.getId(), userDtoFromMethod.getId());
        assertEquals(userDto.getName(), userDtoFromMethod.getName());
        assertEquals(userDto.getEmail(), userDtoFromMethod.getEmail());
        assertEquals(userDto.getToken(), userDtoFromMethod.getToken());
    }

    @Test
    public void testFrom() {
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1L);
        when(user1.getName()).thenReturn("John Doe");
        when(user1.getEmail()).thenReturn("johndoe@example.com");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2L);
        when(user2.getName()).thenReturn("Jane Doe");
        when(user2.getEmail()).thenReturn("janedoe@example.com");

        List<User> users = Arrays.asList(user1, user2);

        FavoriteParkDto parkDto1 = new FavoriteParkDto(new Park(), 1);
        FavoriteParkDto parkDto2 = new FavoriteParkDto(new Park(), 2);
        List<FavoriteParkDto> parksUser1 = List.of(parkDto1);
        List<FavoriteParkDto> parksUser2 = List.of(parkDto2);

        Map<Long, List<FavoriteParkDto>> favoriteParks = new HashMap<>();
        favoriteParks.put(1L, parksUser1);
        favoriteParks.put(2L, parksUser2);

        List<UserDto> result = UserDto.from(users, favoriteParks);

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("johndoe@example.com", result.get(0).getEmail());
        assertEquals("", result.get(0).getToken());
        assertEquals(parksUser1, result.get(0).getFavoriteParks());

        assertEquals("Jane Doe", result.get(1).getName());
        assertEquals("janedoe@example.com", result.get(1).getEmail());
        assertEquals("", result.get(1).getToken());
        assertEquals(parksUser2, result.get(1).getFavoriteParks());
    }
}
