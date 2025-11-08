package com.group.SeatIn.security;

import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidateToken {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public ValidateToken(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Validates a JWT token, ensures it is not expired, and retrieves the associated user.
     *
     * @param token the JWT token, possibly prefixed with "Bearer "
     * @return the User associated with the token
     * @throws RuntimeException if the token is invalid or the user cannot be found
     */
    public User validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Missing Authorization token");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String subject = jwtUtil.extractEmail(token);
        User user = null;

        if (subject != null && !subject.isBlank()) {
            user = userRepository.findByEmail(subject);
        }

        if (user == null) {
            throw new RuntimeException(subject == null || subject.isBlank() ?
                    "Token subject empty and userId fallback failed" :
                    "User not found for subject: " + subject);
        }

        return user;
    }
}