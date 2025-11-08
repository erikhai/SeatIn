package com.group.SeatIn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDetailsDTO {
    private long id;
    private String eventOrganiser;
    private String eventName;
    private String eventDescription;
    private long duration;

    public EventDetailsDTO(long id, String eventOrganiser, String eventName, String eventDescription, long duration) {
        this.id = id;
        this.eventDescription = eventDescription;
        this.eventOrganiser = eventOrganiser;
        this.eventName = eventName;
        this.duration = duration;
    }

}
