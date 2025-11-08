package com.group.SeatIn.controller;

import com.group.SeatIn.dto.EventDTO;
import com.group.SeatIn.dto.TierDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.User;
import com.group.SeatIn.dto.CreateEventDTO;
import com.group.SeatIn.repository.EventRepository;
import com.group.SeatIn.repository.SeatRepository;
import com.group.SeatIn.repository.TierRepository;
import com.group.SeatIn.repository.UserRepository;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers=EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {
    @Autowired
    MockMvc mvc;
    @MockitoBean
    EventService eventService;
    @MockitoBean
    JwtUtil jwtUtil;
    @MockitoBean
    EventRepository eventRepository;
    @MockitoBean
    SeatRepository seatRepository;
    @MockitoBean
    TierRepository tierRepository;
    @MockitoBean
    UserRepository userRepository;


    @Test
    void testGetAnalytics() throws Exception {
        // Create a dummy return for findAnalytics
        HashMap<String, HashMap<String,Float>> analytics = new HashMap<>();
        // Mock the method which is used to simulate the controller
        when(eventService.findEventAnalytics("",(long) 1)).thenReturn(analytics);
        //Ensure status passes as logic has been tested in service tests ensure it matches empty hashmap
        mvc.perform(get("/event/analytics/1").header("Authorization","")).andExpect(status().isOk()).andExpect(content().json("{}"));
    }

    @Test
    void testGetHostingEvents() throws Exception {
        // Create a dummy return for findAnalytics
        List<EventDTO> events = new ArrayList<>();
        // Mock the method which is used to simulate the controller
        when(eventService.findAllHostingEvents("")).thenReturn(events);
        //Ensure status passes as logic has been tested in service tests ensure it matches empty list
        mvc.perform(get("/event/hosting_events").header("Authorization","")).andExpect(status().isOk()).andExpect(status().isOk()).andExpect(content().json("[]"));
    }


    @Test
    void testGetEventTiersAndPrice() throws Exception {
        // Mock the return of tier DTOs for the method that uses get Event Tiers and Price
        List<TierDTO> tierDTOS = new ArrayList<>();
        // Mock the method which is used to simulate the controller
        when(eventService.getAllEventTiers("",1)).thenReturn(tierDTOS);
        //Ensure status passes as logic has been tested in service tests ensure it matches empty list
        mvc.perform(get("/event/get-event-tiers/1").header("Authorization","")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void testGetEventDetails() throws Exception {
        // Create an Event object such that it can be mocked when we test eventRepository
        Event event = new Event("Daltonne House","Hard work pays off", LocalDateTime.now(), (long) 1000, 1,1,null,true,null, "Eric's graduation party");
        // Convert it to DTO Format as this is what event repository will return
        EventDTO eventDTO = new EventDTO(event.getEventId(),event.getEventName(),event.getStart(),event.getDuration(),event.getLocation(),event.getDescription());
        // Mock the method which is used to simulate the controller
        when(eventService.findEvent((long) 1)).thenReturn(eventDTO);
        //Ensure status passes as logic has been tested in service tests
        mvc.perform(get("/event/get-event-details/1").header("Authorization","")).andExpect(status().isOk());
    }

    @Test
    void testGetDetailsForDisplay() throws Exception {
        // Create an Event object such that it can be mocked when we test eventRepository
        Event event = new Event("Daltonne House","Hard work pays off", LocalDateTime.now(), (long) 1000, 1,1,null,true,null, "Eric's graduation party");
        // Convert it to DTO Format as this is what event repository will return
        EventDTO eventDTO = new EventDTO(event.getEventId(),event.getEventName(),event.getStart(),null,event.getImage(),event.getColumns(),event.getDuration(),true,event.getLocation(),event.getRows());
        // Mock the method which is used to simulate the controller
        when(eventService.findEvent((long) 1)).thenReturn(eventDTO);
        //Ensure status passes as logic has been tested in service tests
        mvc.perform(get("/event/description/1").header("Authorization","")).andExpect(status().isOk());
    }

    @Test
    void testGetEventStatus() throws Exception {
        // Mock the boolean return for the service used by the controller method
        when(eventService.checkIfEventRunning("",1)).thenReturn(true);
        //Ensure status passes as logic has been tested in service tests ensure it matches boolean
        mvc.perform(get("/event/get-event-status/1").header("Authorization","")).andExpect(status().isOk()).andExpect(content().string("true"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        Event event = new Event("Venue", "Desc", LocalDateTime.now(), 1000L, 1, 1, null, true, null, "Title");
        when(eventRepository.getReferenceById(1L)).thenReturn(event);
        when(eventService.deleteEvent("", 1L)).thenReturn(true);
        when(tierRepository.findTiersByEvent(event)).thenReturn(new ArrayList<>());
        mvc.perform(post("/event/delete-event")
                        .header("Authorization", "")
                        .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testCreateNewEvent() throws Exception {
        String token = "Bearer testtoken";
        String email = "user@example.com";

        when(jwtUtil.extractSubject("testtoken")).thenReturn(email);
        User user = new User();
        user.setUserId(1L);
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        CreateEventDTO requestDto = new CreateEventDTO();
        CreateEventDTO responseDto = new CreateEventDTO();
        when(eventService.createNewEvent(1L, requestDto)).thenReturn(responseDto);

        mvc.perform(post("/event/create-new-event")
                        .header("Authorization", token)
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEvent() throws Exception {
        Event event = new Event("Venue", "Desc", LocalDateTime.now(), 1000L, 1, 1, null, true, null, "Title");
        when(eventService.getEventById("", "1")).thenReturn(event);

        mvc.perform(get("/event/get-event-detail")
                        .header("Authorization", "")
                        .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.description").value("Desc"))
                .andExpect(jsonPath("$.location").value("Venue"));
    }

    @Test
    void testGetRegisteredEvents() throws Exception {
        List<EventDTO> events = new ArrayList<>();
        when(eventService.findRegisteredEvents("")).thenReturn(events);

        mvc.perform(get("/event/get-registered-events").header("Authorization", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"registeredEvent\":[]}"));
    }

    @Test
    void testGetValidRegisteredEvent() throws Exception {
        EventDTO outcome = new EventDTO(1L, "Sample Event", LocalDateTime.now(), 60L, "Venue", "Description");
        when(eventService.determineUserIsRegisteredToEvent("", "1")).thenReturn(outcome);

        mvc.perform(get("/event/valid-registered-event")
                        .header("Authorization", "")
                        .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"validUserRegistered\":{}}"));
    }

    @Test
    void testGetOrganiser() throws Exception {
        when(eventService.getOrganiser("", "1")).thenReturn("organiser@example.com");

        mvc.perform(get("/event/get-organiser")
                        .header("Authorization", "")
                        .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"organiser\":\"organiser@example.com\"}"));
    }

    @Test
    void testGetAllEvents() throws Exception {
        List<EventDTO> events = new ArrayList<>();
        when(eventService.getAllEvents("")).thenReturn(events);

        mvc.perform(get("/event/get-events").header("Authorization", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

}