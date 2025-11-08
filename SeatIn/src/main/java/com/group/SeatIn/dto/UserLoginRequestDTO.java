package com.group.SeatIn.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginRequestDTO {
    private String email;
    private String password;
}