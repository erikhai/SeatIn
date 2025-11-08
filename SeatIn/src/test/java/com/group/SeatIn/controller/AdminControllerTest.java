package com.group.SeatIn.controller;

import com.group.SeatIn.dto.*;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AdminController adminController;

    private final String token = "Bearer adminToken";
    private final String email = "admin@test.com";
    private final String password = "password123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ReturnsToken_WhenAuthenticated() {
        AdminLoginRequestDTO request = new AdminLoginRequestDTO();
        request.setEmail(email);
        request.setPassword(password);

        AdminDTO mockAdmin = new AdminDTO(email, password);

        when(adminService.authenticate(email, password)).thenReturn(mockAdmin);
        when(jwtUtil.generateToken(email)).thenReturn("jwtToken123");

        ResponseEntity<?> response = adminController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("jwtToken123", body.get("token"));

        verify(adminService, times(1)).authenticate(email, password);
        verify(jwtUtil, times(1)).generateToken(email);
    }

    @Test
    void validate_ReturnsTrue_WhenAdminIsValid() {
        when(adminService.validate(token)).thenReturn(true);

        ResponseEntity<?> response = adminController.validate(token);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(true, body.get("isAdmin"));

        verify(adminService, times(1)).validate(token);
    }

    @Test
    void validate_ReturnsFalse_WhenNotAdmin() {
        when(adminService.validate(token)).thenReturn(false);

        ResponseEntity<?> response = adminController.validate(token);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(false, body.get("isAdmin"));

        verify(adminService, times(1)).validate(token);
    }

    @Test
    void adminSearchUser_ReturnsUser_WhenAdminValid() {
        long userId = 1L;
        String username = "john_doe";
        UserDetailsDTO mockUser = new UserDetailsDTO(userId, username, password);

        when(adminService.validate(token)).thenReturn(true);
        when(adminService.findUser(username)).thenReturn(mockUser);

        ResponseEntity<?> response = adminController.adminSearchUser(token, username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUser, response.getBody());

        verify(adminService, times(1)).validate(token);
        verify(adminService, times(1)).findUser(username);
    }

    @Test
    void adminSearchUser_ReturnsForbidden_WhenNotAdmin() {
        String username = "john_doe";
        when(adminService.validate(token)).thenReturn(false);

        ResponseEntity<?> response = adminController.adminSearchUser(token, username);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Requires Admin Privilege", response.getBody());

        verify(adminService, times(1)).validate(token);
        verify(adminService, never()).findUser(anyString());
    }

    @Test
    void adminSearchEvent_ReturnsEvent_WhenAdminValid() {
        long eventId = 1L;
        String eventOrganiser = "mock organiser";
        String eventName = "Concert";
        String eventDescription = "mock description";
        long duration = 60L;
        EventDetailsDTO mockEvent = new EventDetailsDTO(eventId, eventOrganiser, eventName, eventDescription, duration);
        mockEvent.setEventName(eventName);

        when(adminService.validate(token)).thenReturn(true);
        when(adminService.findEvent(eventName)).thenReturn(mockEvent);

        ResponseEntity<?> response = adminController.adminSearchEvent(token, eventName);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockEvent, response.getBody());

        verify(adminService, times(1)).validate(token);
        verify(adminService, times(1)).findEvent(eventName);
    }

    @Test
    void adminSearchEvent_ReturnsForbidden_WhenNotAdmin() {
        String eventName = "Concert";
        when(adminService.validate(token)).thenReturn(false);

        ResponseEntity<?> response = adminController.adminSearchEvent(token, eventName);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Requires Admin Privilege", response.getBody());

        verify(adminService, times(1)).validate(token);
        verify(adminService, never()).findEvent(anyString());
    }
}
