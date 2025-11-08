package com.group.SeatIn.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.group.SeatIn.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpRequestDTO {
    private String username;
    private String password;
    private String email;

    public SignUpRequestDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
    }
}
