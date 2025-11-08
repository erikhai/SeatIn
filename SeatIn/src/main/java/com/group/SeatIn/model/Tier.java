package com.group.SeatIn.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tier")
@Getter
@Setter
public class Tier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="tier_id", nullable = false, updatable = false)
    private long tierId; // Tier ID

    @Column(name = "hex_colour", length = 7, nullable = false)
    private String hexColour; //Hexadecimal code of tier

    @Column(nullable = false)
    private String tierName;

    @Column(nullable = false)
    private Float price; //Price of the tier

    @ManyToOne(optional = false)
    @JoinColumn(name="event", referencedColumnName = "event_id", nullable = false)
    private Event event; // Foreign key referencing event

    // --- Constructors ---
    public Tier() {}

    public Tier(String hexColour, Float price, Event event) {
        this.event = event;
        this.hexColour = hexColour;
        this.price = price;
    }
}
