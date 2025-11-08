package com.group.SeatIn.service;

import com.beust.ah.A;
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
import com.group.SeatIn.security.ValidateToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    // Classify all repositories and dependenices as mocks as they are needed in the class
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TierRepository tierRepository;
    @Mock
    private SeatRepository seatRepository;
    // Place them into user-service which is the class we are testing and need these dependencies for
    @InjectMocks
    private UserService userService;
    @Mock
    private ValidateToken validateToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Method will be a normal test case checking if correct analytics of a consumer are retrieved
     */
    @Test
    void testUserCustomerAnalytics() {
        // Create our test user
        User test_user = new User("TestMan","test@g","1",false);
        // First pass the JWT login check through mocking it to return true
        when(validateToken.validateToken("")).thenReturn(test_user);
        // Now return this user when retrieving by email
        when(userRepository.findByEmail("test@g")).thenReturn(test_user);
        // Create mock events
        // Now create some an event under this user and one not under the user
        Event event = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 0, 1, 1, null, true, test_user, "SYNCS Hackathon #1");
        event.setEventId((long) 1);
        // Set both eventID and name
        // This event user registered for will be in the past
        Event event_two = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 0, 1, 1, null, true, null, "SYNCS Hackathon #2");
        event_two.setEventId((long) 2);
        // Now create a tier and seat so user can book a seat in an event
        Tier tier = new Tier("123",(float) 10, event_two);
        // Now add a seat in the tier booked by the logged in user
        Seat seat = new Seat(true,tier,test_user,1,1);
        // Add one seat not reserved
        Seat seat_two = new Seat(true,tier,null,1,1);
        // Have a third event which is current
        Event event_three =  new Event("Redfern", "Competition", LocalDateTime.now(), (long) 1000000, 1, 1, null, true, null, "SYNCS Hackathon #2");
        event_three.setEventId((long) 3);
        Tier tier_two = new Tier("122",(float) 21,event_three);
        Seat seat_three = new Seat(true,tier_two,test_user,2,2);
        // Mock some methods in the function
        // Mock the method to return the event the user has registered for
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event,event_two, event_three));
        when(eventRepository.findRegisteredEventByEmailAndEventId("test@g", event_two.getEventId())).thenReturn(event_two);
        when(eventRepository.findRegisteredEventByEmailAndEventId("test@g", event_three.getEventId())).thenReturn(event_three);
        when(eventRepository.getReferenceById(event_two.getEventId())).thenReturn(event_two);
        when(eventRepository.getReferenceById(event_three.getEventId())).thenReturn(event_three);
        when(tierRepository.findTiersByEvent(event_two)).thenReturn(Arrays.asList(tier));
        when(tierRepository.findTiersByEvent(event_three)).thenReturn(Arrays.asList(tier_two));
        when(seatRepository.findSeatsByTier(tier)).thenReturn(Arrays.asList(seat,seat_two));
        when(seatRepository.findSeatsByTier(tier_two)).thenReturn(Arrays.asList(seat_three));
        // Now verify the results
        HashMap<String, Integer> results = userService.userCustomerAnalytics("");
        assertEquals((float) 31, (float) results.get("Spending"));
        assertEquals((float) 1, (float) results.get("Attended"));
        assertEquals((float) 1, (float) results.get("Registered"));
    }

    /**
     * Method will be a normal test case testing host analytics
     */
    @Test
    void testUserHostAnalytics() {
        // Create our test user
        User test_user = new User("TestMan","test@g","1",false);
        // First pass the JWT login check through mocking it to return true
        when(validateToken.validateToken("")).thenReturn(test_user);
        // Create two events one in the past and one current
        // This one will be in the past
        Event event = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 0, 1, 1, null, true, test_user, "SYNCS Hackathon #1");
        event.setEventId((long) 1);
        // Set both eventID and name
        // This one will be current
        Event event_two = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 1000000, 1, 1, null, true, test_user, "SYNCS Hackathon #2");
        event_two.setEventId((long) 2);
        // Now mock all events to return these two events made
        when(eventRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(event,event_two)));
        // Now create tiers in both events
        Tier tier_one = new Tier("1", (float) 10,event);
        Tier tier_two = new Tier("12", (float) 15,event_two);
        // Create a user who will book in both events
        User booking_user = new User("BookingMan", "b@g","1",false);
        // Create seats for both have one booked and one not booked
        Seat seat_one = new Seat(true,tier_one,booking_user,1,1);
        Seat seat_two = new Seat(true,tier_one,null,1,1);
        Seat seat_three = new Seat(true,tier_two,booking_user,1,1);
        Seat seat_four = new Seat(true,tier_two,null,1,1);
        // Now mock the retrieval of tiers and seats
        when(eventRepository.getReferenceById(event.getEventId())).thenReturn(event);
        when(eventRepository.getReferenceById(event_two.getEventId())).thenReturn(event_two);
        when(tierRepository.findTiersByEvent(event)).thenReturn(new ArrayList<>(Arrays.asList(tier_one)));
        when(tierRepository.findTiersByEvent(event_two)).thenReturn(new ArrayList<>(Arrays.asList(tier_two)));
        when(seatRepository.findSeatsByTier(tier_one)).thenReturn(new ArrayList<>(Arrays.asList(seat_one,seat_two)));
        when(seatRepository.findSeatsByTier(tier_two)).thenReturn(new ArrayList<>(Arrays.asList(seat_three,seat_four)));
        // Now compare expected to actual result
        HashMap<String,String> solution = new HashMap<>();
        solution = userService.userHostAnalytics("");
        assertEquals("TestMan",solution.get("User"));
        assertEquals("1",solution.get("Hosted"));
        assertEquals("2",solution.get("Created"));
        assertEquals("1",solution.get("Attendees"));
        assertEquals("25",solution.get("Revenue"));
        assertEquals("SYNCS Hackathon #1",solution.get("Best-Hosted-Event"));
        assertEquals("SYNCS Hackathon #2",solution.get("Best-Current-Event"));
    }
}
