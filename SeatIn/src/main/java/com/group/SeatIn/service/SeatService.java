package com.group.SeatIn.service;


import com.group.SeatIn.dto.SeatDTO;
import com.group.SeatIn.model.Seat;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.SeatRepository;
import com.group.SeatIn.security.ValidateToken;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ValidateToken validateToken;
    /**
     * Method will return all seats associated with an event
     */
    public List<SeatDTO> findRelevantSeat(String token, String eventId) {

        try {
            User user = validateToken.validateToken(token);

            List<Object[]> results = seatRepository.findParticularSeatsFromEventId(user.getEmail(), Long.parseLong(eventId));

            List<SeatDTO> mappedResults = new ArrayList<>();
            for (Object[] r : results) {
                int row = ((Number) r[0]).intValue();
                int column = ((Number) r[1]).intValue();
                String tierName = (String) r[2];
                String tierColour = (String) r[3];
                float tierPrice = ((Number) r[4]).floatValue();

                mappedResults.add(new SeatDTO(row, column, tierName, tierColour, tierPrice));
            }

            return mappedResults;




        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    @Transactional
    public boolean unregisterSeat(String token, String eventId) {
        try {
            User user = validateToken.validateToken(token);
            int seatsFreed = seatRepository.unregisterUserSeats(user.getUserId(), Long.parseLong(eventId));

            return seatsFreed > 0;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    @Transactional
    public boolean bookSeat(String token, String seatId) {
        try {
            User user = validateToken.validateToken(token);
            seatRepository.bookSeat(user.getUserId(), Long.parseLong(seatId));

            return Boolean.TRUE;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public List<SeatDTO> findAllSeats(String token, String eventId) {
        try {
            validateToken.validateToken(token);

            List<Seat> results = seatRepository.findSeatsByEventId(Long.parseLong(eventId));

            List<SeatDTO> mappedResults = new ArrayList<>();
            for (Seat s : results) {
                mappedResults.add(new SeatDTO(s.getSeatId(), s.getRow(),s.getColumn(), s.getTier().getTierName(), s.getTier().getHexColour(), s.getTier().getPrice(), s.isValid(), s.getReservedBy()==null?0:s.getReservedBy().getUserId()));
            }

            return mappedResults;




        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}