package com.group.SeatIn.service;

import com.group.SeatIn.dto.AdminDTO;
import com.group.SeatIn.dto.EventDetailsDTO;
import com.group.SeatIn.dto.UserDetailsDTO;
import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.EventRepository;
import com.group.SeatIn.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import com.group.SeatIn.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AdminService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EventRepository eventRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    /**
     * Authenticate if this is a valid admin
     *
     * @param email is a String of the email of an "admin"
     *
     * @param password is a raw String of the password of an "admin"
     *
     * @return AdminDTO of a user with email and password
     */
    public AdminDTO authenticate(String email, String password) {
        logger.debug("Authenticating admin with email: {}", email);

        User admin = userRepository.findByEmail(email);
        if (admin == null) {
            logger.warn("User not found for email: {}", email);
            throw new RuntimeException("User not found. Invalid email or password.");
        }
        if (!admin.isAdmin()){
            logger.warn("User {} attempted admin login but is not an admin", email);
            throw new RuntimeException("You are not authorised to use this service.");
        }
        String storedHash = admin.getPassword();
        logger.debug("Stored password hash: {}", storedHash);

        if (!passwordEncoder.matches(password, storedHash)) {
            logger.warn("Invalid password for user: {}", email);
            throw new RuntimeException("Invalid email or password.");
        }
        logger.info("Admin {} successfully authenticated", email);
        return new AdminDTO(email, storedHash);
    }


    /**
     * Validates if current user is an admin
     *
     * @param token a map containing key-value pairs from the request body.
     *
     * @return a Boolean TRUE value if this is an admin.
     */
    public Boolean validate(String token) {
        //System.out.println("Validating token");
        try {
            logger.debug("Checking if this token is from an admin account");
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String email = jwtUtil.extractEmail(token);
            User admin = userRepository.findByEmail(email);
            if (admin == null) {
                System.out.println("Admin was null");
                logger.debug("Not found account");
                throw new RuntimeException("User not found.");
            }

            if (!admin.isAdmin()) {
                System.out.println("Account was not admin");
                logger.debug("Not admin account");
                return Boolean.FALSE;
            }

            return Boolean.TRUE;

        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    @Transactional
    public UserDetailsDTO findUser(String username) {
        User user =  userRepository.findByUsername(username);
        if (user == null) {
            return new UserDetailsDTO(-1, "Does not exist", "Does not exist");
        }
        return new UserDetailsDTO(user.getUserId(), user.getUsername(), user.getEmail());
    }

    @Transactional
    public EventDetailsDTO findEvent(String eventName) {
        Event event =  eventRepository.findByEventName(eventName);
        if (event == null) {
            return new EventDetailsDTO(-1, "n/a", "n/a", "n/a", 0);
        }
        User eventOrganiser = event.getOrganiser();
        String organiserUsername = eventOrganiser.getUsername();
        return new EventDetailsDTO(event.getEventId(), organiserUsername, event.getEventName(), event.getDescription(), event.getDuration());
    }

}
