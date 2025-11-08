package com.group.SeatIn.repository;


import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByEventName(String eventName);

    @NativeQuery(value = """
            
            SELECT DISTINCT e.*
            FROM event e
            JOIN tier t ON t.event = e."event_id"
            JOIN seat s ON s.tier = t.tier_id
            JOIN users u ON u.user_id = s.reserved_by
            WHERE u.email = :email;
            """)
    List<Event> findRegisteredEventsByEmail(@Param("email") String email);

    @NativeQuery(value = """

            SELECT DISTINCT e.*
            FROM event e
            JOIN tier t ON t.event = e.event_id
            JOIN seat s ON s.tier = t.tier_id
            JOIN users u ON u.user_id = s.reserved_by
            WHERE u.email = :email and e.event_id = :id;
            """)
    Event findRegisteredEventByEmailAndEventId(@Param("email") String email, @Param("id") long id);

    @Query("SELECT e.organiser FROM Event e WHERE e.eventId = :eventId")
    User findOrganiserByEventId(@Param("eventId") Long eventId);


    Event findEventsByEventId(@Param("eventId") Long eventId);
}