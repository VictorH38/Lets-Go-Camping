package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AllUsersResponseTest {

    @Test
    public void constructorCorrectlyInitializesData() {
        UserDto user1 = new UserDto(1L, "User1", "user1@example.com", "token1");
        UserDto user2 = new UserDto(2L, "User2", "user2@example.com", "token2");
        List<UserDto> users = Arrays.asList(user1, user2);

        AllUsersResponse response = new AllUsersResponse(users);

        assertNotNull(response.getData(), "Data should not be null");
        assertEquals(users, response.getData().getUsers(), "Users should match the constructor argument");
    }

    @Test
    public void setDataCorrectlyUpdatesData() {
        UserDto user1 = new UserDto(1L, "User1", "user1@example.com", "token1");
        UserDto user2 = new UserDto(2L, "User2", "user2@example.com", "token2");
        List<UserDto> usersInitial = Arrays.asList(user1);
        List<UserDto> usersUpdated = Arrays.asList(user1, user2);
        AllUsersResponse response = new AllUsersResponse(usersInitial);

        assertEquals(usersInitial, response.getData().getUsers(), "Initial users should match the constructor argument");

        AllUsersResponse.Data newData = new AllUsersResponse.Data();
        newData.setUsers(usersUpdated);
        response.setData(newData);

        assertEquals(usersUpdated, response.getData().getUsers(), "Users should be updated correctly");
    }
}