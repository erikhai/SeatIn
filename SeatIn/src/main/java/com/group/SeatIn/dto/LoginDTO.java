package com.group.SeatIn.dto;


import com.group.SeatIn.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    private String username;
    private String password;
    private String email;

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;

    }
}
