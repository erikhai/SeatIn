package com.group.SeatIn.dto;

import lombok.Getter;

@Getter
public class AdminLoginResponse {
    private String accessToken;
    public AdminLoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}
