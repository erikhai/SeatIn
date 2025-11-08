package com.group.SeatIn.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CreateEventDTO {
    private String eventName;
    private String description;
    private String location;
    private LocalDateTime start;
    private Long duration;
    private byte[] image;
    private int rows;
    private int columns;
    private List<TierDTO> tiers;
    private List<SeatDTO> selectedSeats;

    public CreateEventDTO(String eventName, String description, String location,
                          LocalDateTime start, Long duration, byte[] image, int rows, int columns,
                          List<TierDTO> tiers, List<SeatDTO> selectedSeats) {
        this.eventName = eventName;
        this.description = description;
        this.location = location;
        this.start = start;
        this.duration = duration;
        this.image = image;
        this.rows = rows;
        this.columns = columns;
        this.tiers = tiers;
        this.selectedSeats = selectedSeats;
    }
}
