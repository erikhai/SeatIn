package com.group.SeatIn.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TierDTO {
    private String name;
    private float price;
    private String color;
    public TierDTO(String name, float price, String color) {
        this.name = name;
        this.price = price;
        this.color = color;
    }
}
