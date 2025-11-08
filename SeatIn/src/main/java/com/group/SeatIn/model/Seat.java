package com.group.SeatIn.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter
@Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seat_id", nullable = false, updatable = false)
    private long seatId; // Tier ID

    @Column(name = "valid", nullable = false)
    private boolean valid; // Is the seat valid

    @Column(name = "row_number", nullable = false)
    private int row;

    @Column(name = "column_number", nullable = false)
    private int column;

    @ManyToOne()
    @JoinColumn(name = "tier", referencedColumnName = "tier_id", nullable = false)
    private Tier tier; // Foreign key referencing tier

    @ManyToOne()
    @JoinColumn(name = "reserved_by", referencedColumnName = "user_id")
    private User reservedBy; // Foreign key referencing user reserving the seat (null if none)

    // --- Constructors ---
    public Seat() {}

    public Seat(boolean valid,  Tier tier, User reservedBy, int row, int column) {
        this.valid = valid;
        this.reservedBy = reservedBy;
        this.tier = tier;
        this.row = row;
        this.column = column;

    }
}