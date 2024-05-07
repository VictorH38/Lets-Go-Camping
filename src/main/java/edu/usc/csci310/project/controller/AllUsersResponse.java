package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.dto.UserDto;

import java.util.List;

public class AllUsersResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public AllUsersResponse(List<UserDto> users) {
        Data data = new AllUsersResponse.Data();
        data.setUsers(users);
        this.data = data;
    }

    public static class Data {
        private List<UserDto> users;

        public List<UserDto> getUsers() {
            return users;
        }

        public void setUsers(List<UserDto> users) {
            this.users = users;
        }
    }
}
