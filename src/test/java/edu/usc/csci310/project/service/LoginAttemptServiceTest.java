package edu.usc.csci310.project.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    void testLoginFailed_IncrementAndBlock() {
        String key = "user@example.com";
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);

        assertTrue(loginAttemptService.isBlocked(key));
    }

    @Test
    void testLoginFailed_NotBlocked() {
        String key = "user2@example.com";
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);

        assertFalse(loginAttemptService.isBlocked(key));
    }

    @Test
    void testLoginSucceeded() {
        String key = "user3@example.com";
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);
        loginAttemptService.loginFailed(key);

        assertTrue(loginAttemptService.isBlocked(key));

        loginAttemptService.loginSucceeded(key);

        assertFalse(loginAttemptService.isBlocked(key));
    }

    @Test
    void testCacheLoader_InitialLoad() throws Exception {
        String key = "new_user@example.com";

        assertEquals(Integer.valueOf(0), loginAttemptService.getAttemptCache().get(key));
        assertEquals(false, loginAttemptService.getBlockCache().get(key));

        assertEquals(false, loginAttemptService.isBlocked(key));
        loginAttemptService.loginFailed(key);
        assertEquals(false, loginAttemptService.isBlocked(key));
    }
}
