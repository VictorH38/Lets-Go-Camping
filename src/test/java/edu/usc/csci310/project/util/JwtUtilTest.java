package edu.usc.csci310.project.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    void testClassConstructor() {
        JwtUtil jwtUtil = new JwtUtil();
        assertNotNull(jwtUtil);
    }

    @Test
    public void testGenerateValidateToken() {
        String email = "test@example.com";
        String token = JwtUtil.generateToken(email);
        assertNotNull(token);

        Claims claims = JwtUtil.validateToken(token);
        assertEquals(email, claims.getSubject());
    }
}
