package com.group.SeatIn.controller;


import com.group.SeatIn.dto.EventDTO;
import com.group.SeatIn.dto.SeatDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.service.AdminService;
import com.group.SeatIn.service.EventService;
import com.group.SeatIn.service.SeatService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/seat")
@CrossOrigin(origins = "http://localhost:3000")
public class SeatController {
    private final SeatService seatService;
    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }
    @GetMapping("/get-all-seats-for-event")
    public ResponseEntity<?> getAllSeatsForEventForUser(@RequestHeader("Authorization") String token, @RequestParam String eventId) {
        List<SeatDTO> seats = seatService.findRelevantSeat(token, eventId);
        return ResponseEntity.ok(Map.of(
                "seats", seats
        ));
    }
    @Transactional(readOnly = true)
    @GetMapping("/get-every-seat-for-event")
    public ResponseEntity<?> getAllSeatsForEvent(@RequestHeader("Authorization") String token, @RequestParam String eventId) {
        List<SeatDTO> seats = seatService.findAllSeats(token, eventId);
        return ResponseEntity.ok(seats);
    }


    @Transactional
    @PostMapping("/book-seat")
    public Boolean bookSeat(@RequestHeader("Authorization") String token, @RequestParam String seatId){
        return  seatService.bookSeat(token, seatId);
    }
    @PatchMapping("/remove-registered-seats")
    public ResponseEntity<?> removeRegisteredSeats(@RequestHeader("Authorization") String token, @RequestBody UnregisterSeatRequest request) {
        boolean response = seatService.unregisterSeat(token, request.eventId);
        return ResponseEntity.ok(Map.of(
                "Success", response
        ));
    }
    @GetMapping("/get-all-seats")
    public ResponseEntity<?> getAllSeats(@RequestHeader("Authorization") String token, @RequestParam String eventId) {
        List<SeatDTO> seats = seatService.findAllSeats(token, eventId);
        return ResponseEntity.ok(Map.of(
                "seats", seats
        ));
    }
    @Getter
    @Setter
    public static class UnregisterSeatRequest {
        private String eventId;

    }
}