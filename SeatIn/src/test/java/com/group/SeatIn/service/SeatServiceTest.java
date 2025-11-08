package com.group.SeatIn.service;

import com.group.SeatIn.dto.SeatDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.Seat;
import com.group.SeatIn.model.Tier;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.SeatRepository;
import com.group.SeatIn.security.ValidateToken;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ValidateToken validateToken;

    @InjectMocks
    private SeatService seatService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User("john_doe", "password", "email@example.com", false);
        mockUser.setUserId(1L);
    }

    @Test
    void testFindRelevantSeat() {
        when(validateToken.validateToken("validToken")).thenReturn(mockUser);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, 2, "VIP", "#FFD700", 100.0f});
        mockResults.add(new Object[]{2, 3, "General", "#00FF00", 50.0f});

        when(seatRepository.findParticularSeatsFromEventId(eq(mockUser.getEmail()), anyLong()))
                .thenReturn(mockResults);

        List<SeatDTO> seats = seatService.findRelevantSeat("validToken", "10");

        assertEquals(2, seats.size());
        assertEquals("VIP", seats.get(0).getTierName());
        assertEquals("#00FF00", seats.get(1).getTierColour());
        assertEquals(50.0f, seats.get(1).getTierPrice());
    }

    @Test
    void testFindRelevantSeat_InvalidToken() {
        when(validateToken.validateToken("invalidToken")).thenThrow(new JwtException("Invalid token"));
        assertThrows(RuntimeException.class, () -> seatService.findRelevantSeat("invalidToken", "10"));
    }

    @Test
    void testUnregisterSeat_Success() {
        when(validateToken.validateToken("validToken")).thenReturn(mockUser);
        when(seatRepository.unregisterUserSeats(mockUser.getUserId(), 20L)).thenReturn(2);

        assertTrue(seatService.unregisterSeat("validToken", "20"));
        verify(seatRepository, times(1)).unregisterUserSeats(mockUser.getUserId(), 20L);
    }

    @Test
    void testUnregisterSeat_NoSeatsFreed() {
        when(validateToken.validateToken("validToken")).thenReturn(mockUser);
        when(seatRepository.unregisterUserSeats(mockUser.getUserId(), 20L)).thenReturn(0);

        assertFalse(seatService.unregisterSeat("validToken", "20"));
    }

    @Test
    void testBookSeat_Success() {
        when(validateToken.validateToken("validToken")).thenReturn(mockUser);

        boolean result = seatService.bookSeat("validToken", "5");

        verify(seatRepository, times(1)).bookSeat(mockUser.getUserId(), 5L);
        assertTrue(result);
    }

    @Test
    void testBookSeat_InvalidToken() {
        when(validateToken.validateToken("invalidToken")).thenThrow(new JwtException("Invalid token"));
        assertThrows(RuntimeException.class, () -> seatService.bookSeat("invalidToken", "5"));
    }

    @Test
    void testFindAllSeats() {
        when(validateToken.validateToken("validToken")).thenReturn(mockUser);

        Event event = new Event();
        event.setEventId(1L);

        Tier tier = new Tier("#FFD700", 100.0f, event);

        Seat s1 = new Seat(true, tier, mockUser, 1, 1);
        s1.setSeatId(1L);
        Seat s2 = new Seat(false, tier, null, 2, 2);
        s2.setSeatId(2L);

        when(seatRepository.findSeatsByEventId(1L)).thenReturn(Arrays.asList(s1, s2));

        List<SeatDTO> dtos = seatService.findAllSeats("validToken", "1");
        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals(1, dtos.get(0).getRow());
        assertEquals(mockUser.getUserId(), dtos.get(0).getReservedBy());
        assertEquals(0, dtos.get(1).getReservedBy());
    }

    @Test
    void testFindAllSeats_InvalidToken() {
        when(validateToken.validateToken("invalidToken")).thenThrow(new JwtException("Invalid token"));
        assertThrows(RuntimeException.class, () -> seatService.findAllSeats("invalidToken", "50"));
    }
}
