package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.User;
import edu.usc.csci310.project.dto.UserDto;
import edu.usc.csci310.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public AllUsersResponse getAllUsers() {
        List<UserDto> users = userService.findAllUsers();
        return new AllUsersResponse(users);
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody User user) {
        return userService.signup(user.getName(), user.getEmail(), user.getPassword());
    }

    @PostMapping("/signin")
    public UserDto signin(@RequestBody SignInRequest req) {
        return userService.signin(req.getEmail(), req.getPassword());
    }

    @PostMapping("/public")
    public void setUserPublic(@RequestBody SetUserPublicRequest req) {
        userService.setUserPublicity(req.getUserId(), req.getIsPublic());
    }
}