package edu.usc.csci310.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usc.csci310.project.dto.UserDto;
import edu.usc.csci310.project.service.UserService;
import edu.usc.csci310.project.util.AccountLockedException;
import edu.usc.csci310.project.util.EmailAlreadyInUseException;
import edu.usc.csci310.project.util.InvalidPasswordException;
import edu.usc.csci310.project.util.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testSignup() throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Name\", \"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSignup_EmailAlreadyInUse() throws Exception {
        String requestBody = "{\"name\":\"Test Name\", \"email\":\"test@example.com\", \"password\":\"password\"}";

        given(userService.signup("Test Name","test@example.com", "password"))
                .willThrow(new EmailAlreadyInUseException("Email already in use"));

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertInstanceOf(EmailAlreadyInUseException.class, result.getResolvedException()));
    }

    @Test
    public void testSignin() throws Exception {
        SignInRequest signInRequest = new SignInRequest("test@example.com", "password");
        UserDto userDto = new UserDto(1L, "Test Name", "test@example.com", "token");

        given(userService.signin(signInRequest.getEmail(), signInRequest.getPassword())).willReturn(userDto);

        mockMvc.perform(post("/api/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    public void testSignin_UserNotFound() throws Exception {
        SignInRequest signInRequest = new SignInRequest("nonexistent@example.com", "password");
        given(userService.signin(signInRequest.getEmail(), signInRequest.getPassword()))
                .willThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/api/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexistent@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertInstanceOf(UserNotFoundException.class, result.getResolvedException()));
    }

    @Test
    public void testSignin_InvalidPassword() throws Exception {
        SignInRequest signInRequest = new SignInRequest("test@example.com", "wrongpassword");
        given(userService.signin(signInRequest.getEmail(), signInRequest.getPassword()))
                .willThrow(new InvalidPasswordException("Invalid password"));

        mockMvc.perform(post("/api/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertInstanceOf(InvalidPasswordException.class, result.getResolvedException()));
    }

    @Test
    public void getAllUsers_ShouldReturnAllUsers() throws Exception {
        String token = "some-generated-token";
        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "User1", "user1@example.com", token),
                new UserDto(2L, "User2", "user2@example.com", token)
        );

        AllUsersResponse expectedResponse = new AllUsersResponse(users);

        given(userService.findAllUsers()).willReturn(users);

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

    }

    @Test
    public void testLockOut() throws Exception {
        SignInRequest signInRequest = new SignInRequest("nonexistent@example.com", "password");
        given(userService.signin(signInRequest.getEmail(), signInRequest.getPassword()))
                .willThrow(new AccountLockedException("Account Locked out"));

        mockMvc.perform(post("/api/users/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexistent@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertInstanceOf(AccountLockedException.class, result.getResolvedException()));
    }

    @Test
    public void testSetUserPublic() throws Exception {
        Long userId = 1L;
        Boolean isPublic = true;

        String jsonPayload = "{\"user_id\":" + userId + ", \"is_public\":" + isPublic + "}";

        doNothing().when(userService).setUserPublicity(userId, isPublic);

        mockMvc.perform(post("/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());
    }
}
