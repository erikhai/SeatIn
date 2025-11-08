package com.group.SeatIn.controller;

import com.group.SeatIn.dto.AdminDTO;
import com.group.SeatIn.dto.AdminLoginRequestDTO;
import com.group.SeatIn.dto.LoginDTO;
import com.group.SeatIn.dto.SignUpRequestDTO;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody CreateSignupRequest request) {
        SignUpRequestDTO surDto = authService.createUser(
                request.username,
                request.email,
                request.password
        );

        // generate token right after signup
        String token = jwtUtil.generateToken(surDto.getEmail());

        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully!",
                "token", token,
                "user", surDto
        ));
    }

    @PostMapping("/login")
    /**
     * Logging in as a user.
     *
     * @param request body which consists of email and password
     *
     * @return A response entity which consists of the jwt (access) token
     */
    public ResponseEntity<?> login(@RequestBody AdminLoginRequestDTO request) {
        LoginDTO user = authService.authenticate(request.getEmail(), request.getPassword());

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        if (jwtUtil.validateToken(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
    }



    public static class CreateSignupRequest {
        public String username;
        public String email;
        public String password;
    }
}

