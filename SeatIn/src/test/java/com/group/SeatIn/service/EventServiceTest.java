package com.group.SeatIn.service;

import com.group.SeatIn.controller.EmailController;
import com.group.SeatIn.dto.CreateEventDTO;
import com.group.SeatIn.dto.EventDTO;
import com.group.SeatIn.dto.SeatDTO;
import com.group.SeatIn.dto.TierDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.Seat;
import com.group.SeatIn.model.Tier;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.EventRepository;
import com.group.SeatIn.repository.SeatRepository;
import com.group.SeatIn.repository.TierRepository;
import com.group.SeatIn.repository.UserRepository;
import com.group.SeatIn.security.ValidateToken;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EventServiceTest {
    // Classify all repositories and dependenices as mocks as they are needed in the class
    @Mock
    private EventRepository eventRepository;
    @Mock
    private ValidateToken validateToken;
    @Mock
    private TierRepository tierRepository;
    @Mock
    private SeatRepository seatRepository;
    // Specify placing these mocks inside the class of testing
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventService eventService;
    @Mock
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test Case will be a normal test case testing if valid event is returned given eventID
     */
    @Test
    void testFindEvent() {
        // Create an Event object such that it can be mocked when we test eventRepository
        Event event = new Event("Daltonne House","Hard work pays off", LocalDateTime.now(), (long) 1000, 1,1,null,true,null, "Eric's graduation party");
        // Convert it to DTO Format as this is what event repository will return
        EventDTO eventDTO = new EventDTO(event);
        // If ID of 1 passed in mock it to return event we created
        when(eventRepository.findById((long) 123)).thenReturn(Optional.ofNullable(event));
        // See if the method returns what we expected
        assertEquals(eventService.findEvent((long) 123).getDescription(),eventDTO.getDescription());
    }

    /**
     * Test case will be a normal test case testing if code can detect an event is not running correctly
     */
    @Test
    void testCheckIfEventExpired() {
        // To test the method we must first pass the token check, will do this by mocking validate token to return null
        when(validateToken.validateToken("")).thenReturn(null);
        // Now must mock what event is returned and we will mock an event which finishes before we can read it
        Event event = new Event("Redfern", "W9 Lecture", LocalDateTime.now(), (long) 0, 1, 1, null, true, null, "ELEC 5619 Lecture");
        when(eventRepository.getReferenceById((long) 123)).thenReturn(event);
        // See if method returns the fact that event is not running anymore
        assertEquals(eventService.checkIfEventRunning("",123),false);

    }

    /**
     * Test case will be a normal test case testing if code can detect an event is still running correctly
     */
    @Test
    void testCheckIfEventRunning() {
        // To test the method we must first pass the token check, will do this by mocking validate token to return null
        when(validateToken.validateToken("")).thenReturn(null);
        // Now must mock what event is returned and we will mock an event which is still running by the time we check it
        Event event = new Event("Redfern", "W9 Lecture", LocalDateTime.now(), (long) 100000, 1, 1, null, true, null, "ELEC 5619 Lecture");
        when(eventRepository.getReferenceById((long) 123)).thenReturn(event);
        // See if method returns the fact that event is still running
        assertEquals(eventService.checkIfEventRunning("",123),true);
    }

    /**
     * Test case will be a normal test case testing if code can retrieve analytics correctly
     */
    @Test
    void testCheckIfAnalyticsCorrectlyRetrieved() {
        // To test the method we must first pass the token check, will do this by mocking validate token to return null
        when(validateToken.validateToken("")).thenReturn(null);
        // First must create an event we want analytics for
        Event event = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 0, 1, 1, null, true, null, "SYNCS Hackathon");
        // Create one tier in this event
        Tier tier = new Tier("123",(float) 12.23, event);
        // This tier will be stage which must be skipped
        Tier tier_skip = new Tier("1233",(float) 0, event);
        tier_skip.setTierName("Stage");
        tier.setTierName("Test");
        List<Tier> tiers = new ArrayList<>(Arrays.asList(tier,tier_skip));
        // Create one seat in this tier
        Seat seat = new Seat(true,tier,null,1,1);
        // Create user for one seat to be booked
        User user = new User("123","123","123",false);
        Seat seat_booked = new Seat(true,tier,user,1,2);
        List<Seat> seats = new ArrayList<>(Arrays.asList(seat,seat_booked));
        // Now mock each method
        when(eventRepository.getReferenceById((long) 123)).thenReturn(event);
        when(tierRepository.findTiersByEvent(event)).thenReturn(tiers);
        when(seatRepository.findSeatsByTier(tier)).thenReturn(seats);
        // Emulate the solution based on the mocks created
        HashMap<String, HashMap<String,Float>> solution = new HashMap<>();
        HashMap<String,Float> sub_solution = new HashMap<>();
        sub_solution.put("Seats", (float) 2);
        sub_solution.put("Booked",(float) 1);
        sub_solution.put("Price", (float) 12.23);
        solution.put("Test", sub_solution);
        // Now ensure the mocked and returned solution the same
        assertEquals(eventService.findEventAnalytics("",123),solution);
    }

    /**
     * Test case will check if code correctly retrieves all hosting events based on user logged in
     */
    @Test
    void testFindAllHostingEvents() {
        // Create a mock user to test finding all hosting events logic
        User user = new User("Eric","123@g","123",false);
        User user_two = new User("Safo","12@g","1",false);
        // Make sure user created above is returned when we run validate token as the logic of the method needs this
        // As this functionality is based on logged in user
        when(validateToken.validateToken("")).thenReturn(user);
        // Now create some an event under this user and one not under the user
        Event event = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 0, 1, 1, null, true, user, "SYNCS Hackathon #1");
        event.setEventId((long) 1);
        // Set both eventID and name
        Event event_two = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 0, 1, 1, null, true, user_two, "SYNCS Hackathon #2");
        event_two.setEventId((long) 2);
        List<Event> events = new ArrayList<>(Arrays.asList(event,event_two));
        // Retrieve all mocked events when calling eventRepository
        when(eventRepository.findAll()).thenReturn(events);
        // Make sure user is returned when called:
        // Map EVENT #1 with user who created it (Eric)
        when(eventRepository.findOrganiserByEventId((long) 1)).thenReturn(user);
        // Map EVENT #2 with user who created it (Safo)
        when(eventRepository.findOrganiserByEventId((long) 2)).thenReturn(user_two);
        // Assert only one event is returned
        assertEquals(1,eventService.findAllHostingEvents("").size());
        // Assert the event returned is the one created by our mocked user, eric
        EventDTO event_returned = eventService.findAllHostingEvents("").get(0);
        assertEquals(event_returned.getEName(),"SYNCS Hackathon #1");
    }

    /**
     * Test will check if code returns all tiers given event
     */
    @Test
    void testGetAllEventTiers() {
        // First pass the login stage
        when(validateToken.validateToken("")).thenReturn(null);
        // Now create an event and associate it with tiers
        Event event = new Event("Redfern", "Competition", LocalDateTime.now(), (long) 0, 1, 1, null, true, null, "SYNCS Hackathon");
        // Create one tier in this event
        Tier tier = new Tier("123",(float) 12.23, event);
        tier.setTierName("Test");
        // This tier will be stage which must be skipped
        Tier tier_skip = new Tier("1233",(float) 0, event);
        tier_skip.setTierName("Stage");
        List<Tier> tiers = new ArrayList<>(Arrays.asList(tier,tier_skip));
        // Now mock the return of the event through ID and ensure tiers returned for the event
        when(eventRepository.getReferenceById((long) 1)).thenReturn(event);
        when(tierRepository.findTiersByEvent(event)).thenReturn(tiers);
        // Now ensure only one tier was returned
        assertEquals(1, eventService.getAllEventTiers("",(long) 1).size());
        // Ensure the only tier matches test which is the only valid one as stage is not to be displayed
        assertEquals(eventService.getAllEventTiers("", (long) 1).get(0).getName(),"Test");
    }

    @Test
    void testCreateNewEvent() {
        Long userId = 1L;

        User mockUser = new User("john_doe", "password", "email@example.com", false);
        mockUser.setUserId(userId);

        CreateEventDTO createEventDTO = new CreateEventDTO();
        createEventDTO.setEventName("Music Fest");
        createEventDTO.setDescription("A fun event");
        createEventDTO.setStart(LocalDateTime.now());
        createEventDTO.setDuration(120L);
        createEventDTO.setLocation("Sydney");
        createEventDTO.setColumns(5);
        createEventDTO.setRows(5);
        createEventDTO.setImage(null);

        TierDTO tier1 = new TierDTO("VIP", 100.0f, "#FFD700");
        TierDTO tier2 = new TierDTO("General", 50.0f, "#00FF00");
        createEventDTO.setTiers(Arrays.asList(tier1, tier2));

        SeatDTO seat1 = new SeatDTO(1, 1, "VIP");
        SeatDTO seat2 = new SeatDTO(1, 2, "General");
        createEventDTO.setSelectedSeats(Arrays.asList(seat1, seat2));

        Event savedEvent = new Event();
        savedEvent.setEventId(99L);
        when(userRepository.getReferenceById(userId)).thenReturn(mockUser);
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(eventRepository.getReferenceById(99L)).thenReturn(savedEvent);

        Tier vipTier = new Tier("VIP", 100.0f, savedEvent);
        Tier generalTier = new Tier("General", 50.0f, savedEvent);
        when(tierRepository.findByEvent_EventIdAndTierName(99L, "VIP")).thenReturn(vipTier);
        when(tierRepository.findByEvent_EventIdAndTierName(99L, "General")).thenReturn(generalTier);

        CreateEventDTO result = eventService.createNewEvent(userId, createEventDTO);

        verify(eventRepository, times(1)).save(any(Event.class));
        verify(tierRepository, times(2)).save(any(Tier.class));
        verify(seatRepository, times(2)).save(any(Seat.class));

        assertEquals(createEventDTO.getEventName(), result.getEventName());
        assertEquals(createEventDTO.getTiers().size(), result.getTiers().size());
        assertEquals(createEventDTO.getSelectedSeats().size(), result.getSelectedSeats().size());

        assertEquals("VIP", tier1.getName());
        assertEquals("#FFD700", tier1.getColor());
    }

    @Test
    void testFindRegisteredEvents_Success() {
        String token = "validToken";
        String email = "user@example.com";

        User mockUser = new User("username", email, "hashedPass", false);
        when(validateToken.validateToken(token)).thenReturn(mockUser);

        User organiser = new User("organiser", "org@example.com", "pass", false);
        Event event = new Event("Venue", "Desc", LocalDateTime.now(), 1000L, 3, 3, null, true, organiser, "Event Name");
        event.setEventId(1L);
        List<Event> eventList = List.of(event);
        when(eventRepository.findRegisteredEventsByEmail(email)).thenReturn(eventList);

        List<EventDTO> result = eventService.findRegisteredEvents(token);

        assertEquals(1, result.size());
        assertEquals("Event Name", result.get(0).getEName());
        verify(validateToken).validateToken(token);
        verify(eventRepository).findRegisteredEventsByEmail(email);
    }

    @Test
    void testFindRegisteredEvents_InvalidToken() {
        String token = "invalidToken";
        when(validateToken.validateToken(token)).thenThrow(new JwtException("Invalid token"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventService.findRegisteredEvents(token)
        );

        assertEquals("Invalid token", ex.getMessage().substring(0, 13)); // partial match ok
    }

    @Test
    void testDeleteEvent_Success() {
        String token = "validToken";
        long eventId = 1L;


        when(validateToken.validateToken(token)).thenReturn(new User("test", "t@e.com", "p", false));
        User organiser = new User("org", "org@example.com", "p", false);
        Event event = new Event("Venue", "Desc", LocalDateTime.now(), 1000L, 1, 1, null, true, organiser, "Test Event");
        when(eventRepository.getReferenceById(eventId)).thenReturn(event);

        User user1 = new User("Alice", "a@example.com", "pass", false);
        Seat seat1 = new Seat();
        seat1.setReservedBy(user1);
        List<Seat> seatList = List.of(seat1);
        when(seatRepository.findSeatsByEventId(eventId)).thenReturn(seatList);
        boolean result = eventService.deleteEvent(token, eventId);

        assertTrue(result);
        verify(validateToken).validateToken(token);
        verify(eventRepository).getReferenceById(eventId);
        verify(seatRepository).findSeatsByEventId(eventId);
        verify(emailController).sendEventCancelledEmailRequestBody(event, user1);
    }

    @Test
    void testDeleteEvent_InvalidToken() {
        String token = "invalidToken";
        when(validateToken.validateToken(token)).thenThrow(new JwtException("Invalid token"));

        assertThrows(JwtException.class, () ->
                eventService.deleteEvent(token, 123L)
        );
    }

    @Test
    void testGetAllEvents_Success() {
        String token = "validToken";
        when(validateToken.validateToken(token)).thenReturn(new User("u", "u@example.com", "p", false));

        User organiser = new User("org", "org@example.com", "p", false);
        Event event = new Event("Venue", "Desc", LocalDateTime.now(), 1000L, 2, 2, null, true, organiser, "All Event");
        event.setEventId(1L);
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<EventDTO> result = eventService.getAllEvents(token);

        assertEquals(1, result.size());
        assertEquals("All Event", result.get(0).getEName());
        verify(validateToken).validateToken(token);
        verify(eventRepository).findAll();
    }

    @Test
    void testDetermineUserIsRegisteredToEvent_Success() {
        String token = "validToken";
        String email = "test@example.com";
        String eventId = "1";

        User mockUser = new User("tester", email, "p", false);
        when(validateToken.validateToken(token)).thenReturn(mockUser);

        Event event = new Event("Venue", "Desc", LocalDateTime.now(), 1000L, 2, 2, null, true, mockUser, "Registered Event");
        event.setEventId(1L);
        when(eventRepository.findRegisteredEventByEmailAndEventId(email, 1L)).thenReturn(event);

        EventDTO result = eventService.determineUserIsRegisteredToEvent(token, eventId);

        assertNotNull(result);
        assertEquals("Registered Event", result.getEName());
        verify(validateToken).validateToken(token);
        verify(eventRepository).findRegisteredEventByEmailAndEventId(email, 1L);
    }

    @Test
    void testDetermineUserIsRegisteredToEvent_NotRegistered() {
        String token = "validToken";
        String email = "test@example.com";
        String eventId = "99";

        User mockUser = new User("tester", email, "p", false);
        when(validateToken.validateToken(token)).thenReturn(mockUser);
        when(eventRepository.findRegisteredEventByEmailAndEventId(email, 99L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventService.determineUserIsRegisteredToEvent(token, eventId)
        );

        assertEquals("User is not registered for this event.", ex.getMessage());
    }





}

