package com.group.SeatIn.dto;

import com.group.SeatIn.model.Seat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeatDTO {

    private String hexColour;
    private int row;
    private int column;
    private String tierName;
    private String tierColour;
    private float tierPrice;
    private boolean valid;
    private long reservedBy;
    private long id;

    // Constructor
    public SeatDTO(int row, int column, String tierName, String tierColour, float tierPrice) {
        this.row = row;
        this.column = column;
        this.tierName = tierName;
        this.tierColour = tierColour;
        this.tierPrice = tierPrice;
    }


    public SeatDTO(int row, int col, String tierName) {
        this.row = row;
        this.column = col;
        this.tierName = tierName;
    }

    public SeatDTO(int row, int column, String tierName, String hexColour, Float price, boolean valid) {
        this.row = row;
        this.column = column;
        this.tierName = tierName;
        this.hexColour = hexColour;
        this.tierPrice = price;
        this.valid = valid;
    }
    public SeatDTO(long id, int row, int column, String tierName, String hexColour, Float price, boolean valid, long reservedBy) {
        this.row = row;
        this.column = column;
        this.tierName = tierName;
        this.hexColour = hexColour;
        this.tierPrice = price;
        this.valid = valid;
        this.reservedBy = reservedBy;
        this.id = id;
    }
}