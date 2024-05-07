package edu.usc.csci310.project.service;

import edu.usc.csci310.project.domain.User;
import edu.usc.csci310.project.dto.FavoriteParkDto;
import edu.usc.csci310.project.dto.UserDto;
import edu.usc.csci310.project.repository.UserRepository;
import edu.usc.csci310.project.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ParkService parkService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    public UserDto signup(String name, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User(name, email, hashedPassword);
        userRepository.save(newUser);

        String token = JwtUtil.generateToken(newUser.getEmail());
        return UserDto.from(newUser, token);
    }

    public UserDto signin(String email, String password) {
        if (loginAttemptService.isBlocked(email)) {
            throw new AccountLockedException("Account is temporarily locked");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        boolean isPasswordMatch = passwordEncoder.matches(password, user.getPassword());
        if (!isPasswordMatch) {
            loginAttemptService.loginFailed(email);
            throw new InvalidPasswordException("Invalid password");
        }
        loginAttemptService.loginSucceeded(email);
        String token = JwtUtil.generateToken(email);
        return UserDto.from(user, token);
    }

    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        Map<Long, List<FavoriteParkDto>> userParksMap = new HashMap<>();
        for (User user : users) {
            long userId = user.getId();
            userParksMap.put(userId, parkService.getFavoriteParks(userId));
        }

        return UserDto.from(users, userParksMap);
    }

    public void setUserPublicity(Long userId, Boolean isPublic) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setIsPublic(isPublic);
            userRepository.save(user.get());
        }
    }
}