package com.group.SeatIn.service;

import com.group.SeatIn.dto.AdminDTO;
import com.group.SeatIn.dto.LoginDTO;
import com.group.SeatIn.dto.SignUpRequestDTO;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.AuthRepository;
import com.group.SeatIn.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;

    }
    @Transactional
    public SignUpRequestDTO createUser(String username, String email, String password) {
        User user = new User(username, email, passwordEncoder.encode(password), false);
        if (authRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (authRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        user = authRepository.save(user);
        return new SignUpRequestDTO(user);
    }

    /**
     * Authenticate if this is a valid admin
     *
     * @param email is a String of the email of an "admin"
     *
     * @param password is a raw String of the password of an "admin"
     *
     * @return AdminDTO of a user with email and password
     */
    public LoginDTO authenticate(String email, String password) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found. Invalid email or password.");
        }

        String storedHash = user.getPassword();

        if (!passwordEncoder.matches(password, storedHash)) {
            throw new RuntimeException("Invalid email or password.");
        }
        return new LoginDTO(email, storedHash);
    }
}