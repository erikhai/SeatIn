package com.group.SeatIn.controller;


import com.group.SeatIn.dto.CreateEventDTO;
import com.group.SeatIn.dto.EventDTO;
import com.group.SeatIn.dto.TierDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.Seat;
import com.group.SeatIn.model.Tier;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.EventRepository;
import com.group.SeatIn.repository.SeatRepository;
import com.group.SeatIn.repository.TierRepository;
import com.group.SeatIn.repository.UserRepository;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.service.AdminService;
import com.group.SeatIn.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/event")
@CrossOrigin(origins = "http://localhost:3000")
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final TierRepository tierRepository;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;

    @Autowired
    public EventController(EventService eventService, JwtUtil jwtUtil, UserRepository userRepository, EventRepository eventRepository, SeatRepository seatRepository, TierRepository tierRepository) {
        this.eventService = eventService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.tierRepository = tierRepository;
    }

    @GetMapping("/get-event-detail")
    public ResponseEntity<?> getEvent(@RequestHeader("Authorization") String token, @RequestParam String eventId){
        Event response = eventService.getEventById(token, eventId);
        return ResponseEntity.ok(Map.of(
                "title", response.getEventName(),
                "description", response.getDescription(),
                "location", response.getLocation(),
                "start", response.getStart(),
                "duration", response.getDuration()
        ));

    }

    @PostMapping("/create-new-event")
    public ResponseEntity<?> createNewEvent(@RequestHeader("Authorization") String token,
                                            @RequestBody CreateEventDTO createEventDTO) {
        String jwt = token.replace("Bearer ", "");
        String email = jwtUtil.extractSubject(jwt);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        CreateEventDTO createdEvent = eventService.createNewEvent(user.getUserId(), createEventDTO);
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping("/get-registered-events")
    public ResponseEntity<?> getRegisteredEvents(@RequestHeader("Authorization") String token) {
        List<EventDTO> registeredEvent = eventService.findRegisteredEvents(token);
        return ResponseEntity.ok(Map.of(
                "registeredEvent", registeredEvent
        ));
    }
    @GetMapping("/valid-registered-event")
    public ResponseEntity<?> getValidRegisteredEvent(@RequestHeader("Authorization") String token, @RequestParam String eventId) {
        EventDTO outcome = eventService.determineUserIsRegisteredToEvent(token, eventId);
        return ResponseEntity.ok(Map.of(
                "validUserRegistered", outcome
        ));
    }

    /**
     * Method will given event_id will return all information related to it
     * @param event_id
     * @param event_id
     * @return
     */
    @GetMapping("/description/{event_id}")
    public ResponseEntity<?> getDetailsForDisplay(@RequestHeader("Authorization") String token, @PathVariable long event_id) {
        EventDTO eventDTO = eventService.findEvent(event_id);
        return ResponseEntity.ok(eventDTO.getDescription());
    }

    /**
     * Method will given event id return all analytics for an event
     * @param event_id
     * @param event_id
     * @return
     */
    @GetMapping("/analytics/{event_id}")
    public ResponseEntity<?> getAnalytics(@RequestHeader("Authorization") String token, @PathVariable long event_id) {
        HashMap<String,HashMap<String,Float>> analytics = eventService.findEventAnalytics(token, event_id);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Method will return all events being hosted by logged in user
     * @param token
     * @return
     */
    @GetMapping("/hosting_events")
    public ResponseEntity<?> getEventsHosting(@RequestHeader("Authorization") String token) {
        List<EventDTO> events = eventService.findAllHostingEvents(token);
        return ResponseEntity.ok(events);
    }

    /**
     * Method will retrieve all current events
     * @param token
     * @return
     */
    @GetMapping("/get-events")
    public ResponseEntity<?> getAllEvents(@RequestHeader("Authorization") String token) {
        List<EventDTO> events = eventService.getAllEvents(token);
        return ResponseEntity.ok(events);
    }

    /**
     * Method will retrieve all details of event
     * @param token
     * @param event_id
     * @return
     */
    @GetMapping("/get-event-details/{event_id}")
    public ResponseEntity<?> getAllEventDetails(@RequestHeader("Authorization") String token, @PathVariable long event_id) {
        EventDTO event = eventService.findEvent(event_id);
        return ResponseEntity.ok(event);
    }

    /**
     * Method will retrieve all tiers and their prices in an event
     * @param token
     * @param event_id
     * @return
     */
    @GetMapping("/get-event-tiers/{event_id}")
    public ResponseEntity<?> getEventTiersAndPrice(@RequestHeader("Authorization") String token, @PathVariable long event_id) {
        List<TierDTO> tiers = eventService.getAllEventTiers(token,event_id);
        return ResponseEntity.ok(tiers);
    }

    /**
     * Method will check whether an event is currently running or has finished
     * @param token
     * @param event_id
     * @return
     */
    @GetMapping("/get-event-status/{event_id}")
    public ResponseEntity<?> getEventStatus(@RequestHeader("Authorization") String token, @PathVariable long event_id) {
        boolean status = eventService.checkIfEventRunning(token,event_id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/get-organiser")
    /**
     * Retrieves the organiser of a given event.
     *
     * @param token the JWT access token provided in the Authorization header
     * @param eventId the ID of the event
     * @return ResponseEntity containing a map with the key "organiser"
     *         and the organiser's identifier (e.g., username or email)
     */
    public ResponseEntity<?> getOrganiser(@RequestHeader("Authorization") String token, @RequestParam String eventId) {
        String outcome = eventService.getOrganiser(token, eventId);
        return ResponseEntity.ok(Map.of(
                "organiser", outcome
        ));
    }

    @PostMapping("/delete-event")
    public ResponseEntity<?> deleteEvent(@RequestHeader("Authorization") String token, @RequestParam Long eventId) {
        boolean outcome = eventService.deleteEvent(token, eventId);
        Event event = eventRepository.getReferenceById(eventId);
        List<Tier> tiers = tierRepository.findTiersByEvent(event);
        List<Seat> seats = new ArrayList<>();
        for (Tier tier : tiers) {
            seats.addAll(seatRepository.findSeatsByTier(tier));
        }
        seatRepository.deleteAll(seats);
        tierRepository.deleteAll(tiers);
        eventRepository.delete(event);

        return ResponseEntity.ok(outcome);
    }

}

