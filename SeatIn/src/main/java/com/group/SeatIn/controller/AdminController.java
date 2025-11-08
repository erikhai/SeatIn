package com.group.SeatIn.controller;

import com.group.SeatIn.dto.AdminDTO;
import com.group.SeatIn.dto.AdminLoginRequestDTO;
import com.group.SeatIn.dto.EventDetailsDTO;
import com.group.SeatIn.dto.UserDetailsDTO;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    /**
     * Logging in as an administrator.
     *
     * @param request body which consists of email and password
     *
     * @return A response entity which consists of the jwt (access) token
     */
    public ResponseEntity<?> login(@RequestBody AdminLoginRequestDTO request) {
        AdminDTO admin = adminService.authenticate(request.getEmail(), request.getPassword());

        String token = jwtUtil.generateToken(admin.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token
        ));
    }

    @GetMapping("/search-user/{username}")
    public ResponseEntity<?> adminSearchUser(@RequestHeader("Authorization") String token, @PathVariable String username) {
        boolean isAdmin = adminService.validate(token);
        if (! isAdmin) {
            System.out.println("Admin privs not found");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requires Admin Privilege");
        }
        UserDetailsDTO userDetailsDTO = adminService.findUser(username);
        return ResponseEntity.ok(userDetailsDTO);
    }

    @GetMapping("/search-event/{eventName}")
    public ResponseEntity<?> adminSearchEvent(@RequestHeader("Authorization") String token, @PathVariable String eventName) {
        boolean isAdmin = adminService.validate(token);
        if (! isAdmin) {
            System.out.println("Admin privs not found");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requires Admin Privilege");
        }
        EventDetailsDTO eventDetailsDTO = adminService.findEvent(eventName);
        return ResponseEntity.ok(eventDetailsDTO);
    }

    @GetMapping("/validate")
    /**
     * Ensuring the current user is an administrator based on the access token provided.
     *
     * @param request header which consists of the access token.
     *
     * @return A response entity that consists of whether this account is an admin.
     */
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String token) {
        Boolean isAdmin = adminService.validate(token);
        return ResponseEntity.ok(Map.of(
                "isAdmin", isAdmin
        ));

    }


}
