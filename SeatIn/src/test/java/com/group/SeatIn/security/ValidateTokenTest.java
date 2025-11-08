package com.group.SeatIn.security;

import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidateTokenTest {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private ValidateToken validateToken;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        validateToken = new ValidateToken(userRepository, jwtUtil);
    }

    @Test
    void testValidateTokenWithValidBearerToken() {
        String token = "Bearer validToken";
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(jwtUtil.validateToken("validToken")).thenReturn(true);
        when(jwtUtil.extractEmail("validToken")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = validateToken.validateToken(token);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testValidateTokenWithValidTokenWithoutBearer() {
        String token = "validToken";
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = validateToken.validateToken(token);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testValidateTokenWithMissingToken() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> validateToken.validateToken(null));
        assertEquals("Missing Authorization token", exception.getMessage());
    }

    @Test
    void testValidateTokenWithInvalidToken() {
        String token = "invalidToken";
        when(jwtUtil.validateToken(token)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> validateToken.validateToken(token));
        assertEquals("Invalid or expired token", exception.getMessage());
    }

    @Test
    void testValidateTokenWithUserNotFound() {
        String token = "validToken";
        String email = "user@example.com";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> validateToken.validateToken(token));
        assertEquals("User not found for subject: " + email, exception.getMessage());
    }

    @Test
    void testValidateTokenWithEmptySubject() {
        String token = "validToken";

        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.extractEmail(token)).thenReturn("");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> validateToken.validateToken(token));
        assertEquals("Token subject empty and userId fallback failed", exception.getMessage());
    }
}
