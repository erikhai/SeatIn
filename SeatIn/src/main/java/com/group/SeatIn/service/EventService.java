package com.group.SeatIn.service;


import com.beust.ah.A;
import com.group.SeatIn.controller.EmailController;
import com.group.SeatIn.dto.*;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.User;
import com.group.SeatIn.model.Tier;
import com.group.SeatIn.model.Seat;
import com.group.SeatIn.repository.EventRepository;
import com.group.SeatIn.repository.UserRepository;
import com.group.SeatIn.repository.SeatRepository;
import com.group.SeatIn.repository.TierRepository;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.security.ValidateToken;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.transaction.Transactional;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TierRepository tierRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private EmailController emailController;
    @Autowired
    private ValidateToken validateToken;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Transactional
    public Event getEventById(String token, String eventId) {
        validateToken.validateToken(token);
        return eventRepository.findEventsByEventId(Long.parseLong(eventId));
    }
    public List<EventDTO> getAllEvents(String token) {
        validateToken.validateToken(token);
        List<EventDTO> events =  new ArrayList<>();
        List<Event> repo = eventRepository.findAll();
        for (Event event : repo) {
            events.add(new EventDTO(event, event.getOrganiser().getUsername()));
        }
        return events;
    }
    
    @Transactional
    public CreateEventDTO createNewEvent(Long userId, CreateEventDTO createEventDTO) {
        Event newEvent = new Event();
        newEvent.setOrganiser(userRepository.getReferenceById(userId));
        newEvent.setEventName(createEventDTO.getEventName());
        newEvent.setDescription(createEventDTO.getDescription());
        newEvent.setStart(createEventDTO.getStart());
        newEvent.setDuration(createEventDTO.getDuration());
        newEvent.setLocation(createEventDTO.getLocation());
        newEvent.setColumns(createEventDTO.getColumns());
        newEvent.setRows(createEventDTO.getRows());
        newEvent.setImage(createEventDTO.getImage());
        eventRepository.save(newEvent);
        Long eventId = newEvent.getEventId();

        List<TierDTO> eventTiers = createEventDTO.getTiers();

        for (TierDTO tierDTO : eventTiers) {
            Tier tier = new Tier();
            tier.setEvent(eventRepository.getReferenceById(eventId));
            tier.setTierName(tierDTO.getName());
            tier.setPrice(tierDTO.getPrice());
            //change?
            tier.setHexColour(tierDTO.getColor());
            tierRepository.save(tier);
        }

        List<SeatDTO> seats = createEventDTO.getSelectedSeats();
        for (SeatDTO seatDTO : seats) {
            Seat seat = new Seat();
            seat.setRow(seatDTO.getRow());
            seat.setColumn(seatDTO.getColumn());
            seat.setTier(tierRepository.findByEvent_EventIdAndTierName(eventId, seatDTO.getTierName()));
            seatRepository.save(seat);
        }
        return createEventDTO;
    }

    /**
     * Method will return all events user is registered to
     */
    @Transactional
    public List<EventDTO> findRegisteredEvents(String token) {

        try {
            User user = validateToken.validateToken(token);
            String email = user.getEmail();
            List<Event> rows = eventRepository.findRegisteredEventsByEmail(email);
            List<EventDTO> events = new ArrayList<>();
            for (Event event : rows) {

                events.add(new EventDTO(event.getEventId(), event.getEventName(), event.getStart(), event.getOrganiser().getUsername(), event.getImage()));
            }
            return events;


        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
    /**
     * Method will return the event the user is registered to (if there is one)
     */
    @Transactional
    public EventDTO determineUserIsRegisteredToEvent(String token, String eventId) {
        try {
            User user = validateToken.validateToken(token);
            String email = user.getEmail();
            Event event = eventRepository.findRegisteredEventByEmailAndEventId(email, Long.valueOf(eventId));
            if (event == null) {
                throw new RuntimeException("User is not registered for this event.");
            }
            return new EventDTO(event);

        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    /**
     * Method will return all details of event given event id
     */
    @Transactional
    public EventDTO findEvent(long event_id) {
        Event event = eventRepository.findById(event_id).orElseThrow();
        EventDTO eventDTO = new EventDTO(event);
        return eventDTO;
    }

    /**
     * Method will return all analytics of event based on tier, where each tier will have several analytics
     * @param event_id
     * @return
     */
    @Transactional
    public HashMap<String,HashMap<String,Float>> findEventAnalytics(String token, long event_id) {
        // Check if token is valid, do it by removing 'Bearer' from string
        validateToken.validateToken(token);
        HashMap<String,HashMap<String,Float>> analytics = new HashMap<>();
        // Will retrieve all tiers that an event has
        List<Tier> eventTiers = tierRepository.findTiersByEvent(eventRepository.getReferenceById(event_id));
        // Based on each tier compute statistics required
        for(Tier tier: eventTiers) {
            //If tier is stage do not compute analytics for it
            if(tier.getTierName().equals("Stage")) {
                continue;
            }
            HashMap<String,Float> tierAnalytics = new HashMap<>();
            List<Seat> seatsByTier = seatRepository.findSeatsByTier(tier);
            float total_seats = seatsByTier.size();
            float price = tier.getPrice();
            float booked = 0;
            // If it has a reservedBy value then we can conclude it is booked
            for(Seat seat: seatsByTier) {
                if(seat.getReservedBy() != null) booked += 1;
            }
            tierAnalytics.put("Seats",total_seats);
            tierAnalytics.put("Booked",booked);
            tierAnalytics.put("Price",price);
            analytics.put(tier.getTierName(),tierAnalytics);
        }
        return analytics;
    }

    /**
     * Method will return whether or not event is still in the future or has finished
     */
    @Transactional
    public boolean checkIfEventRunning(String token, long eventId) {
        // Check if token is valid, do it by removing 'Bearer' from string
        validateToken.validateToken(token);
        // Get the event instance from ID
        Event event = eventRepository.getReferenceById(eventId);
        LocalDateTime end = event.getStart().plusMinutes(event.getDuration());
        // If event end date is before current time this means event has ended
        if(end.isBefore(LocalDateTime.now())) {
            return false;
        }
        // Otherwise it is still running
        return true;
    }

    /**
     * Method will username of event organiser
     */
    @Transactional
    public String getOrganiser(String token, String eventId) {
        validateToken.validateToken(token);


        User organiser = eventRepository.findOrganiserByEventId(Long.valueOf(eventId));
        return organiser.getUsername();
    }

    /**
     * Method will return all events user is hosting given user id
     */
    @Transactional
    public List<EventDTO> findAllHostingEvents(String token) {
        List<EventDTO> events = new ArrayList<>();
        //Check if authentication token is valid by removing 'bearer' from start of token
        User user = validateToken.validateToken(token);
        //Otherwise fetch all events
        List<Event> allEvents = eventRepository.findAll();
        //Find the user who owns the token
        // If any event organiser matches the user logged in add the event to load
        for(Event event: allEvents) {
            if(event.getOrganiser().equals(user)) {
                User organiser = eventRepository.findOrganiserByEventId(event.getEventId());
                String organiserUsername = organiser.getUsername();
                // Ensure structure matches required for displaying all events
                events.add(new EventDTO(event.getEventId(),event.getEventName(),event.getStart(),organiserUsername, event.getImage()));
            }
        }
        return events;
    }

    /**
     * Method will return all tiers for an event given event id
     */
    @Transactional
    public List<TierDTO> getAllEventTiers(String token, long event_id) {
        // Check if token is valid, do it by removing 'Bearer' from string
        validateToken.validateToken(token);
        // Retrieve all tiers from event
        List<Tier> tiers = tierRepository.findTiersByEvent(eventRepository.getReferenceById(event_id));
        List<TierDTO> tierDTOs = new ArrayList<>();
        for(Tier tier: tiers) {
            // Do not add the tier if it is stage as should not display this to user
            if(tier.getTierName().equals("Stage")) {
                continue;
            }
            tierDTOs.add(new TierDTO(tier.getTierName(),tier.getPrice(),tier.getHexColour()));
        }
        return tierDTOs;
    }
    /**
     * Method will return delete an event and all associated tiers and seats
     */
    @Transactional
    public boolean deleteEvent(String token, long event_id) {
            // Check if token is valid, do it by removing 'Bearer' from string
            validateToken.validateToken(token);
            Set<User> users = new HashSet<User>();
            Event event = eventRepository.getReferenceById(event_id);
            List<Seat> seats = seatRepository.findSeatsByEventId(event_id);
            for (Seat seat : seats) {
                if (seat.getReservedBy() != null)
                    users.add(seat.getReservedBy());
            }
            for (User user : users)
                emailController.sendEventCancelledEmailRequestBody(event, user);
            return true;
    }
}