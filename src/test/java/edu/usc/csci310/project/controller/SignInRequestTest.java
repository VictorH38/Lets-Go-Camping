package edu.usc.csci310.project.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SignInRequestTest {

    @Test
    void testGetterAndSetter() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("test@example.com");
        signInRequest.setPassword("password");

        assertEquals("test@example.com", signInRequest.getEmail());
        assertEquals("password", signInRequest.getPassword());
    }

    @Test
    void testConstructor() {
        SignInRequest signInRequest = new SignInRequest("test@example.com", "password");

        assertEquals("test@example.com", signInRequest.getEmail());
        assertEquals("password", signInRequest.getPassword());
    }
}
