package com.group.SeatIn.dto;


import com.group.SeatIn.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDTO {
    private String username;
    private String password;
    private String email;

    public AdminDTO(String email, String password) {
        this.email = email;
        this.password = password;

    }
}
