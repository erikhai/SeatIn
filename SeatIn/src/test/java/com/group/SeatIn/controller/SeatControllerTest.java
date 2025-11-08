package com.group.SeatIn.controller;

import com.group.SeatIn.dto.SeatDTO;
import com.group.SeatIn.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SeatControllerTest {

    @Mock
    private SeatService seatService;

    @InjectMocks
    private SeatController seatController;

    private final String token = "Bearer fakeToken";
    private final String eventId = "event123";
    private final String seatId = "seat999";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSeatsForEventForUser_ReturnsSeatsMap() {
        List<SeatDTO> mockSeats = List.of(new SeatDTO(), new SeatDTO());
        when(seatService.findRelevantSeat(token, eventId)).thenReturn(mockSeats);

        ResponseEntity<?> response = seatController.getAllSeatsForEventForUser(token, eventId);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("seats"));
        assertEquals(mockSeats, body.get("seats"));

        verify(seatService, times(1)).findRelevantSeat(token, eventId);
    }

    @Test
    void getAllSeatsForEvent_ReturnsSeatList() {
        List<SeatDTO> mockSeats = List.of(new SeatDTO());
        when(seatService.findAllSeats(token, eventId)).thenReturn(mockSeats);

        ResponseEntity<?> response = seatController.getAllSeatsForEvent(token, eventId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockSeats, response.getBody());

        verify(seatService, times(1)).findAllSeats(token, eventId);
    }

    @Test
    void bookSeat_ReturnsTrue_WhenBookingSucceeds() {
        when(seatService.bookSeat(token, seatId)).thenReturn(true);

        Boolean result = seatController.bookSeat(token, seatId);

        assertTrue(result);
        verify(seatService, times(1)).bookSeat(token, seatId);
    }

    @Test
    void bookSeat_ReturnsFalse_WhenBookingFails() {
        when(seatService.bookSeat(token, seatId)).thenReturn(false);

        Boolean result = seatController.bookSeat(token, seatId);

        assertFalse(result);
        verify(seatService, times(1)).bookSeat(token, seatId);
    }

    @Test
    void removeRegisteredSeats_ReturnsSuccessTrue() {
        SeatController.UnregisterSeatRequest request = new SeatController.UnregisterSeatRequest();
        request.setEventId(eventId);

        when(seatService.unregisterSeat(token, eventId)).thenReturn(true);

        ResponseEntity<?> response = seatController.removeRegisteredSeats(token, request);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(true, body.get("Success"));

        verify(seatService, times(1)).unregisterSeat(token, eventId);
    }

    @Test
    void getAllSeats_ReturnsSeatsMap() {
        List<SeatDTO> mockSeats = List.of(new SeatDTO(), new SeatDTO());
        when(seatService.findAllSeats(token, eventId)).thenReturn(mockSeats);

        ResponseEntity<?> response = seatController.getAllSeats(token, eventId);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("seats"));
        assertEquals(mockSeats, body.get("seats"));

        verify(seatService, times(1)).findAllSeats(token, eventId);
    }
}

