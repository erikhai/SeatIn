package com.group.SeatIn.dto;

import com.group.SeatIn.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class AdminLoginRequestDTO {
    private String email;
    private String password;
    public AdminLoginRequestDTO(User user) {
        this.password = user.getPassword();
        this.email = user.getEmail();
    }
}
