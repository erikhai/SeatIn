package com.group.SeatIn.service;

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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;

@Service
public class UserService {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TierRepository tierRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ValidateToken validateToken;


    /**
     * Method will retrieve all statistics for attending and registered user given user id
     */
    @Transactional
    public HashMap<String, Integer> userCustomerAnalytics(String token) {
        // Check if token is valid, do it by removing 'Bearer' from string
        //Find the user who owns the token
        User user = validateToken.validateToken(token);
        String email = user.getEmail();
        HashMap<String,Integer> analytics = new HashMap<>();
        //Get all events
        List<Event> allEvents = eventRepository.findAll();
        // First fetch all events the user has registered for / attended:
        int total_spending = 0; int events_attended = 0; int events_registered = 0;
        for(Event event: allEvents) {
            // If the user has registered will return Event object and not NULL
            if(eventRepository.findRegisteredEventByEmailAndEventId(email,event.getEventId())!=null) {
                List<Tier> eventTiers = tierRepository.findTiersByEvent(eventRepository.getReferenceById(event.getEventId()));
                for(Tier tier: eventTiers) {
                    List<Seat> seatsByTier = seatRepository.findSeatsByTier(tier);
                    for(Seat seat: seatsByTier) {
                        // If found a seat belonging to user increment total spending
                        if(seat.getReservedBy() != null && seat.getReservedBy().equals(user)) total_spending += tier.getPrice();
                    }
                }
                // Now check if event has already happened or not
                //If happened then we can say user attended otherwise they are just registered
                LocalDateTime end = event.getStart().plusMinutes(event.getDuration());
                if(end.isBefore(LocalDateTime.now())) events_attended += 1;
                else events_registered += 1;
            }
        }
        analytics.put("Spending", total_spending);
        analytics.put("Attended",events_attended);
        analytics.put("Registered",events_registered);
        return analytics;
    }


    /**
     * Method will retrieve all statistics for host given user id
     */
    @Transactional
    public HashMap<String,String> userHostAnalytics(String token) {
        // Check if token is valid, do it by removing 'Bearer' from string
        //Find the user who owns the token
        User user = validateToken.validateToken(token);
        HashMap<String,String> analytics = new HashMap<>();
        //Get all events
        List<Event> allEvents = eventRepository.findAll();
        // Now fetch all events the user is the host for:
        int total_events_hosted = 0; int total_events_created = 0; int total_attendees = 0; int total_revenue = 0;
        // Store for the best current performing events and events in the past
        String best_hosted_event = "None"; String best_current_event = "None";
        int most_revenue_old = 0; int most_revenue_new = 0;
        for(Event event: allEvents) {
            // Find all events hosted/hosting by user
            if(event.getOrganiser().equals(user)) {
                int current_event_revenue = 0; int event_attendees = 0;
                total_events_created += 1;
                List<Tier> eventTiers = tierRepository.findTiersByEvent(eventRepository.getReferenceById(event.getEventId()));
                // Get tiers in event
                for(Tier tier: eventTiers) {
                    List<Seat> seatsByTier = seatRepository.findSeatsByTier(tier);
                    int booked = 0;
                    for(Seat seat: seatsByTier) {
                        if(seat.getReservedBy() != null) booked += 1;
                    }
                    current_event_revenue += booked * tier.getPrice(); event_attendees += booked;
                }
                total_revenue += current_event_revenue;
                // See whether this is a current or past event
                LocalDateTime end = event.getStart().plusMinutes(event.getDuration());
                // If event has ended before then this is one hosted in the past
                if(end.isBefore(LocalDateTime.now())) {
                    // Only add attendees if event has finished
                    total_events_hosted += 1; total_attendees += event_attendees;
                    if(current_event_revenue > most_revenue_old) {
                        most_revenue_old = current_event_revenue; best_hosted_event = event.getEventName();
                    }
                }
                else {
                    //Otherwise this is a current event not yet hosted/finished
                    if(current_event_revenue > most_revenue_new) {
                        most_revenue_new = current_event_revenue; best_current_event = event.getEventName();
                    }
                }
            }
        }
        analytics.put("User",user.getUsername());
        analytics.put("Hosted",String.valueOf(total_events_hosted));
        analytics.put("Created",String.valueOf(total_events_created));
        analytics.put("Attendees",String.valueOf(total_attendees));
        analytics.put("Revenue",String.valueOf(total_revenue));
        analytics.put("Best-Hosted-Event",best_hosted_event);
        analytics.put("Best-Current-Event",best_current_event);
        return analytics;
    }
}
