package edu.usc.csci310.project.configs;

import edu.usc.csci310.project.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GlobalExceptionHandlerTest.DummyController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenEmailAlreadyInUseExceptionIsThrown_thenRespondWithUnauthorized() throws Exception {
        mockMvc.perform(get("/dummy/email-in-use")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenUserNotFoundExceptionIsThrown_thenRespondWithUnauthorized() throws Exception {
        mockMvc.perform(get("/dummy/user-not-found")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenInvalidPasswordExceptionIsThrown_thenRespondWithUnauthorized() throws Exception {
        mockMvc.perform(get("/dummy/invalid-password")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenParkNotFoundExceptionIsThrown_thenRespondWithUnauthorized() throws Exception {
        mockMvc.perform(get("/dummy/park-not-found")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAccountLockedExceptionIsThrown_thenRespondWithUnauthorized() throws Exception {
        mockMvc.perform(get("/dummy/account-locked")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @RestController
    static class DummyController {

        @GetMapping("/dummy/email-in-use")
        public void dummyEmailInUse() {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        @GetMapping("/dummy/user-not-found")
        public void dummyUserNotFound() {
            throw new UserNotFoundException("User not found");
        }

        @GetMapping("/dummy/invalid-password")
        public void dummyInvalidPassword() {
            throw new InvalidPasswordException("Invalid password");
        }

        @GetMapping("/dummy/park-not-found")
        public void dummyParkNotFound() {
            throw new ParkNotFoundException("No data available for park ID: 1");
        }

        @GetMapping("/dummy/account-locked")
        public void dummyAccountLocked() {
            throw new AccountLockedException("Account is temporarily locked");
        }
    }
}
