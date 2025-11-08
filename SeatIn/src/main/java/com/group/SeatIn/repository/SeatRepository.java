package com.group.SeatIn.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import com.group.SeatIn.model.Seat;
import com.group.SeatIn.model.Tier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Method will just match Seats with Tier since Seats reference tier
     * @param tier
     * @return
     */
    List<Seat> findSeatsByTier(Tier tier);
    @NativeQuery("""
SELECT s.row_number, s.column_number, t.tier_name, t.hex_colour, t.price
FROM seat s
JOIN tier t ON s.tier = t.tier_id
JOIN event e ON t.event = e.event_id
JOIN users u ON s.reserved_by = u.user_id
WHERE u.email = :email and e.event_id = :id;
""")
    List<Object[]> findParticularSeatsFromEventId(@Param("email") String email, @Param("id") long id);

    @Modifying
    @Transactional
    @NativeQuery("""
UPDATE seat
SET reserved_by = NULL
WHERE reserved_by = :userId
AND tier IN (SELECT tier_id
      FROM tier
      WHERE event = :eventId
);
""")
    int unregisterUserSeats(@Param("userId") long userId, @Param("eventId") long eventId);

    @Query("SELECT s FROM Seat s WHERE s.tier.event.eventId = :eventId")
    List<Seat> findSeatsByEventId(@Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @NativeQuery("""
UPDATE seat
SET reserved_by = :userId
WHERE seat_id = :seatId
""")
    int bookSeat(Long userId, long seatId);
}