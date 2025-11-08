package com.group.SeatIn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsDTO {
    private long userId;
    private String userName;
    private String email;

    public UserDetailsDTO(long userId, String userName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
    }

}
