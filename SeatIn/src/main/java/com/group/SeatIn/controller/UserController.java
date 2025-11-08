package com.group.SeatIn.controller;

import com.group.SeatIn.dto.EventDTO;
import com.group.SeatIn.dto.SeatDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")

public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Method will retrieve all events for user based on events they have attended or registered for
     * @param token
     * @return Statistics in the form of a hashmap such that it can be rendered by front-end
     */
    @GetMapping("/get_customer_statistics")
    public ResponseEntity<?> getCustomerStatistics(@RequestHeader("Authorization") String token) {
        HashMap<String,Integer> statistics = userService.userCustomerAnalytics(token);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Method will retrieve all events user has hosted or is hosting
     * @param token
     * @return Statistics in the form of a hashmap such that it can be rendered by front-end
     */
    @GetMapping("/get_host_statistics")
    public ResponseEntity<?> getHostStatistics(@RequestHeader("Authorization") String token) {
        HashMap<String,String> statistics = userService.userHostAnalytics(token);
        return ResponseEntity.ok(statistics);
    }
}
