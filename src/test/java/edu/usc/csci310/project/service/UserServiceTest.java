package edu.usc.csci310.project.service;

import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.domain.User;
import edu.usc.csci310.project.dto.FavoriteParkDto;
import edu.usc.csci310.project.dto.UserDto;
import edu.usc.csci310.project.repository.UserRepository;
import edu.usc.csci310.project.util.AccountLockedException;
import edu.usc.csci310.project.util.InvalidPasswordException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private ParkService parkService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Test
    public void testSignup() {
        String name = "Test Name";
        String email = "test@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword";

        when(userRepository.findByEmail(email)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        userService.signup(name, email, password);

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testSignin() {
        String email = "test@example.com";
        String password = "password";
        User user = new User("Test Name", email, password);

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertDoesNotThrow(() -> userService.signin(email, password));
    }

    @Test
    public void testSignupWithExistingEmail() {
        String name = "Test Name";
        String email = "test@example.com";
        String password = "password";

        User existingUser = new User(name, email, password);
        when(userRepository.findByEmail(email)).thenReturn(existingUser);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.signup(name, email, password);
        });

        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    public void testSigninWithNonExistentEmail() {
        String email = "nonexistent@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.signin(email, password);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testSigninWithIncorrectPassword() {
        String email = "test@example.com";
        String password = "incorrectPassword";
        User user = new User("Test Name", email, "correctPassword");

        when(userRepository.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.signin(email, password);
        });

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    public void findAllUsers_ReturnsUserDtoList() {
        User user1 = new User(1L, "John Doe", "john@example.com", "123");
        User user2 = new User(2L, "Jane Doe", "jane@example.com", "123");
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<FavoriteParkDto> parksUser1 = Arrays.asList(new FavoriteParkDto(new Park(), 1));
        List<FavoriteParkDto> parksUser2 = Arrays.asList(new FavoriteParkDto(new Park(), 2));

        when(parkService.getFavoriteParks(user1.getId())).thenReturn(parksUser1);
        when(parkService.getFavoriteParks(user2.getId())).thenReturn(parksUser2);

        List<UserDto> userDtos = userService.findAllUsers();

        verify(userRepository).findAll();
        verify(parkService).getFavoriteParks(user1.getId());
        verify(parkService).getFavoriteParks(user2.getId());

        assertEquals(2, userDtos.size());
        assertEquals(user1.getId(), userDtos.get(0).getId());
        assertEquals(user1.getName(), userDtos.get(0).getName());
        assertEquals(parksUser1, userDtos.get(0).getFavoriteParks());

        assertEquals(user2.getId(), userDtos.get(1).getId());
        assertEquals(user2.getName(), userDtos.get(1).getName());
        assertEquals(parksUser2, userDtos.get(1).getFavoriteParks());
    }

    @Test
    public void testSignin_WhenAccountIsLocked_ShouldThrowException() {
        String email = "test@example.com";
        String password = "password";

        when(loginAttemptService.isBlocked(email)).thenReturn(true);

        Exception exception = assertThrows(AccountLockedException.class, () -> {
            userService.signin(email, password);
        });

        assertEquals("Account is temporarily locked", exception.getMessage());
        verify(loginAttemptService, times(1)).isBlocked(email);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void whenUserExists_setUserPublicity() {
        Long userId = 1L;
        Boolean isPublic = true;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setIsPublic(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.setUserPublicity(userId, isPublic);

        verify(userRepository).findById(userId);
        assertEquals(isPublic, mockUser.getIsPublic());
        verify(userRepository).save(mockUser);
    }

    @Test
    void whenUserDoesNotExist_doNothing() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userService.setUserPublicity(userId, true);

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }
}
