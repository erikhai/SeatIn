package com.group.SeatIn.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(SecurityConfig.class) // ensure the configuration is loaded
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void testPasswordEncoderBean() {
        assertNotNull(passwordEncoder);
        String raw = "testPassword";
        String encoded = passwordEncoder.encode(raw);
        assertTrue(passwordEncoder.matches(raw, encoded));
    }

    @Test
    void testCorsConfigurationSource() {
        SecurityConfig securityConfig = new SecurityConfig();
        CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/any-path");

        CorsConfiguration config = corsSource.getCorsConfiguration(request);

        assertNotNull(config);
        assertTrue(config.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getAllowCredentials());
    }

    @Test
    void testSecurityFilterChainBean() {
        assertNotNull(securityFilterChain);
    }
}
