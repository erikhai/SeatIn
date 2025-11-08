package com.group.SeatIn.service;

import com.group.SeatIn.dto.AdminDTO;
import com.group.SeatIn.dto.EventDetailsDTO;
import com.group.SeatIn.dto.UserDetailsDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.EventRepository;
import com.group.SeatIn.repository.UserRepository;
import com.group.SeatIn.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AdminService adminService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void testAuthenticateSuccessful() {
        String rawPassword = "password";
        String encodedPassword = encoder.encode(rawPassword);
        User admin = new User("adminUser", "admin@example.com", encodedPassword, true);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(admin);
        AdminDTO result = adminService.authenticate("admin@example.com", rawPassword);
        assertEquals("admin@example.com", result.getEmail());
    }

    @Test
    void testAuthenticateUserNotFound() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.authenticate("admin@example.com", "password"));
        assertEquals("User not found. Invalid email or password.", ex.getMessage());
    }

    @Test
    void testAuthenticateNotAdmin() {
        String encodedPassword = encoder.encode("password");
        User user = new User("normalUser", "user@example.com", encodedPassword, false);
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.authenticate("user@example.com", "password"));
        assertEquals("You are not authorised to use this service.", ex.getMessage());
    }

    @Test
    void testAuthenticateInvalidPassword() {
        String encodedPassword = encoder.encode("correctPassword");
        User admin = new User("adminUser", "admin@example.com", encodedPassword, true);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(admin);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.authenticate("admin@example.com", "wrongPassword"));
        assertEquals("Invalid email or password.", ex.getMessage());
    }

    @Test
    void testValidateSuccessfulAdminToken() {
        User admin = new User("adminUser", "admin@example.com", "password", true);
        when(jwtUtil.extractEmail("validToken")).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(admin);
        Boolean result = adminService.validate("Bearer validToken");
        assertTrue(result);
    }

    @Test
    void testValidateUserNotFound() {
        when(jwtUtil.extractEmail("token")).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.validate("Bearer token"));
        assertEquals("User not found.", ex.getMessage());
    }

    @Test
    void testValidateNotAdminAccount() {
        User user = new User("normalUser", "user@example.com", "password", false);
        when(jwtUtil.extractEmail("validToken")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        Boolean result = adminService.validate("Bearer validToken");
        assertFalse(result);
    }

    @Test
    void testValidateInvalidToken() {
        when(jwtUtil.extractEmail("badToken")).thenThrow(new JwtException("Invalid token"));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.validate("Bearer badToken"));
        assertTrue(ex.getMessage().contains("Invalid token"));
    }
    @Test
    void testFindUser_UserExists() {
        User mockUser = new User("testuser", "password", "test@example.com", false);
        mockUser.setUserId(10L);

        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        UserDetailsDTO result = adminService.findUser("testuser");

        assertEquals(10L, result.getUserId());
        assertEquals("testuser", result.getUserName());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindUser_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        UserDetailsDTO result = adminService.findUser("unknown");

        assertEquals(-1, result.getUserId());
        assertEquals("Does not exist", result.getUserName());
        assertEquals("Does not exist", result.getEmail());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void testFindEvent_EventExists() {
        User organiser = new User("organiserUser", "pass", "org@example.com", false);
        organiser.setUserId(1L);

        Event mockEvent = new Event();
        mockEvent.setEventId(5L);
        mockEvent.setEventName("Tech Expo");
        mockEvent.setDescription("Annual technology showcase");
        mockEvent.setDuration(120L);
        mockEvent.setOrganiser(organiser);

        when(eventRepository.findByEventName("Tech Expo")).thenReturn(mockEvent);

        EventDetailsDTO result = adminService.findEvent("Tech Expo");

        assertEquals(5L, result.getId());
        assertEquals("organiserUser", result.getEventOrganiser());
        assertEquals("Tech Expo", result.getEventName());
        assertEquals("Annual technology showcase", result.getEventDescription());
        assertEquals(120L, result.getDuration());
        verify(eventRepository, times(1)).findByEventName("Tech Expo");
    }

    @Test
    void testFindEvent_EventNotFound() {
        when(eventRepository.findByEventName("Unknown Event")).thenReturn(null);

        EventDetailsDTO result = adminService.findEvent("Unknown Event");

        assertEquals(-1, result.getId());
        assertEquals("n/a", result.getEventOrganiser());
        assertEquals("n/a", result.getEventName());
        assertEquals("n/a", result.getEventDescription());
        assertEquals(0L, result.getDuration());
        verify(eventRepository, times(1)).findByEventName("Unknown Event");
    }

}

