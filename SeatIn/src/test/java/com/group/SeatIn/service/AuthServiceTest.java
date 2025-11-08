package com.group.SeatIn.service;

import com.group.SeatIn.dto.LoginDTO;
import com.group.SeatIn.dto.SignUpRequestDTO;
import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.AuthRepository;
import com.group.SeatIn.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthRepository authRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authRepository = mock(AuthRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        authService = new AuthService(authRepository, passwordEncoder);
        ReflectionTestUtils.setField(authService, "userRepository", userRepository);
    }

    @Test
    void testCreateUser() {
        when(authRepository.existsByUsername("erik")).thenReturn(false); // Used for mocking --> So for this instance when this function is called, instead of access db, simply return false
        when(authRepository.existsByEmail("erik@example.com")).thenReturn(false); // Used for mocking --> So for this instance when this function is called, instead of access db, simply return false
        when(passwordEncoder.encode("password")).thenReturn("hashed password"); // Used for mocking --> So for this instance when this function is called, instead of access db, simply return hashed password

        User savedUser = new User("erik", "erik@example.com", "hashed password", false);
        when(authRepository.save(Mockito.any(User.class))).thenReturn(savedUser); // Used for mocking --> So for this instance when this function is called, instead of access db, simply "save" the user

        SignUpRequestDTO dto = authService.createUser("erik", "erik@example.com", "hashed password");

        assertThat(dto.getUsername()).isEqualTo("erik");
        assertThat(dto.getEmail()).isEqualTo("erik@example.com");
        assertThat(dto.getPassword()).isEqualTo("hashed password");
    }

    @Test
    void testUsernameExists() {

        when(authRepository.existsByUsername("erik")).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.createUser("erik", "erik@example.com", "secret")
        );

        assertEquals("Username already exists", exception.getMessage());
    }
    @Test
    void testEmailExists() {
        when(authRepository.existsByUsername("erik")).thenReturn(false);
        when(authRepository.existsByEmail("erik@example.com")).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.createUser("erik", "erik@example.com", "secret")
        );

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";
        String hashedPassword = "hashed_password";

        User mockUser = new User("testUser", email, hashedPassword, false);

        // Mock repository and encoder behavior
        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        // Act
        LoginDTO result = authService.authenticate(email, rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(hashedPassword, result.getPassword());

        // Verify interactions
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        String email = "missing@example.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.authenticate(email, password)
        );

        assertEquals("User not found. Invalid email or password.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String hashedPassword = "hashed_password";

        User mockUser = new User("testUser", email, hashedPassword, false);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.authenticate(email, rawPassword)
        );

        assertEquals("Invalid email or password.", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, hashedPassword);
    }



}
