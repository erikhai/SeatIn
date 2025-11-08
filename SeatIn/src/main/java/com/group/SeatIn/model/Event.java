package com.group.SeatIn.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="event_id", nullable = false, updatable = false)
    private Long eventId; //Event ID

    @Column(name ="e_name", length = 30, nullable = false)
    private String eventName;

    @Column(name = "location", length = 250, nullable = false)
    private String location; // VARCHAR(250) location

    @Column(name = "e_description", length = 1000)
    private String description; // VARCHAR(1000) description

    @Column(name = "start", nullable = false)
    private LocalDateTime start; // DATETIME start date & time

    @Column(name = "duration", nullable = false)
    private Long duration; // BIGINT duration in minutes

    @Column(name = "rows", nullable = false)
    private int rows; //Number of rows

    @Column(name = "columns", nullable = false)
    private int columns; //Number of columns

    @Lob
    @Column(name = "image")
    private byte[] image; // Event IMAGE

    @Column(name = "is_public", nullable = false)
    private boolean isPublic; // BOOLEAN is public

    @ManyToOne(optional = false)
    @JoinColumn(name = "organiser", referencedColumnName = "user_id", nullable = false)
    private User organiser; // Foreign key referencing user

    // --- Constructors ---
    public Event() {}

    public Event(String location, String description, LocalDateTime start,
                 Long duration, int rows, int columns, byte[] image, boolean isPublic, User organiser, String eventName) {
        this.location = location;
        this.description = description;
        this.start = start;
        this.duration = duration;
        this.rows = rows;
        this.columns = columns;
        this.image = image;
        this.isPublic = isPublic;
        this.organiser = organiser;
        this.eventName = eventName;
    }
}
