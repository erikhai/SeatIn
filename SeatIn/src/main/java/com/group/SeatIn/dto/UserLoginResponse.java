package com.group.SeatIn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponse {
    private String token;
    private String email;
    private String role;
}
