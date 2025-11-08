package com.group.SeatIn.dto;


import lombok.Getter;
import lombok.Setter;
import com.group.SeatIn.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class EventDTO {

    private  Long eventId;
    private  int rows;
    private String description;
    private String eName;
    private LocalDateTime start;
    private String organiser;
    private byte[] image;
    private int columns;
    private Long duration;
    private boolean isPublic;
    public String location;

    // We will convert it to readable time
    private String start_time;

    public EventDTO(Event event) {
        this.eventId = event.getEventId();
        this.eName = event.getEventName();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.start = event.getStart();
        this.duration = event.getDuration();
        this.rows = event.getRows();
        this.columns = event.getColumns();
        this.image = event.getImage();
        // Store it in string format such that it can be rendered for frontend
        this.start_time = start.format(DateTimeFormatter.ofPattern("dd MMM YYYY, H:mm a"));
    }

    public EventDTO(Event event, String organiserName) {
        this.eventId = event.getEventId();
        this.eName = event.getEventName();
        this.description = event.getDescription();
        this.location = event.getLocation();
        this.start = event.getStart();
        this.duration = event.getDuration();
        this.rows = event.getRows();
        this.columns = event.getColumns();
        this.image = event.getImage();
        // Store it in string format such that it can be rendered for frontend
        this.start_time = start.format(DateTimeFormatter.ofPattern("dd MMM YYYY, H:mm a"));
        this.organiser = organiserName;
    }

    public EventDTO(Long eventId, String eName, LocalDateTime start, String organiser, byte[] image) {
        this.eventId = eventId;
        this.eName = eName;
        this.start = start;
        this.image = image;
        this.organiser = organiser;


    }
    public EventDTO(Long eventId, String eName, LocalDateTime start, Long duration,  String location, String description) {
        this.eventId = eventId;
        this.eName = eName;
        this.start = start;
       // this.image = image;
        this.description = description;
        this.duration = duration;
        this.location = location;

    }
    public EventDTO(Long eventId, String eName, LocalDateTime start, String organiser, byte[] image, int columns,  Long duration, boolean isPublic, String location, int rows) {
        this.eventId = eventId;
        this.eName = eName;
        this.start = start;
        this.image = image;
        this.organiser = organiser;
        this.columns = columns;
        this.rows = rows;
        this.duration = duration;
        this.isPublic = isPublic;
        this.location = location;
    }
}
