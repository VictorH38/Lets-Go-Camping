package edu.usc.csci310.project.dto;

import edu.usc.csci310.project.domain.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String token;

    private Boolean isPublic;
    private List<FavoriteParkDto> favoriteParks;

    public UserDto() {}

    public UserDto(Long id, String name, String email, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token = token;
    }

    public UserDto(Long id, String name, String email, String token, List<FavoriteParkDto> favoriteParks, Boolean isPublic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.token = token;
        setFavoriteParks(favoriteParks);
        this.isPublic = isPublic;
    }

    public static UserDto from(User user, String token) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), token);
    }

    public static List<UserDto> from(List<User> users, Map<Long, List<FavoriteParkDto>> favoriteParks) {
        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail(), "", favoriteParks.get(user.getId()), user.getIsPublic()))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<FavoriteParkDto> getFavoriteParks() {
        return favoriteParks;
    }

    public void setFavoriteParks(List<FavoriteParkDto> favoriteParks) {
        this.favoriteParks = favoriteParks;
    }

    public Boolean getIsPublic() { return isPublic; };

    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; };
}
