package com.group.SeatIn.service;

import com.group.SeatIn.model.User;
import com.group.SeatIn.security.ValidateToken;
import com.group.SeatIn.service.EmailService;
import com.sendgrid.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {


    @Test
    void testSendEmailWithQr() throws IOException, NoSuchFieldException, IllegalAccessException {

        ValidateToken validateToken = mock(ValidateToken.class);
        User testUser = new User("test", "test@gmail.com", "1", false);
        when(validateToken.validateToken("")).thenReturn(testUser);

        EmailService emailService = new EmailService();
        var field = EmailService.class.getDeclaredField("validateToken");
        field.setAccessible(true);
        field.set(emailService, validateToken);


        var apiField = EmailService.class.getDeclaredField("sendGridApiKey");
        apiField.setAccessible(true);
        apiField.set(emailService, "FAKE_API_KEY");

        var fromField = EmailService.class.getDeclaredField("fromEmail");
        fromField.setAccessible(true);
        fromField.set(emailService, "noreply@test.com");

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class, (mock, context) -> {
            Response mockResponse = new Response(202, "Accepted", null);
            when(mock.api(any(Request.class))).thenReturn(mockResponse);
        })) {

            String result = emailService.sendEmailWithQr(
                    "Test Subject",
                    "<html><body>Hello QR</body></html>",
                    "base64EncodedQrImage",
                    ""
            );

            assertTrue(result.contains("Status: 202"));
        }
    }
    @Test
    void testSendEmailForUnregisteringEvent() throws IOException, NoSuchFieldException, IllegalAccessException {

        ValidateToken validateToken = mock(ValidateToken.class);
        User testUser = new User("test", "test@gmail.com", "1", false);
        when(validateToken.validateToken("")).thenReturn(testUser);

        EmailService emailService = new EmailService();
        var field = EmailService.class.getDeclaredField("validateToken");
        field.setAccessible(true);
        field.set(emailService, validateToken);


        var apiField = EmailService.class.getDeclaredField("sendGridApiKey");
        apiField.setAccessible(true);
        apiField.set(emailService, "FAKE_API_KEY");

        var fromField = EmailService.class.getDeclaredField("fromEmail");
        fromField.setAccessible(true);
        fromField.set(emailService, "noreply@test.com");

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class, (mock, context) -> {
            Response mockResponse = new Response(202, "Accepted", null);
            when(mock.api(any(Request.class))).thenReturn(mockResponse);
        })) {

            String result = emailService.sendEmailForUnregisteringEvent(
                    "Test Subject",
                    "<html><body>Hello QR</body></html>",
                    ""
            );
            assertTrue(result.contains("Status: 202"));
        }
    }
    @Test
    void testSendCancelEmailForUser() throws IOException, NoSuchFieldException, IllegalAccessException {

        ValidateToken validateToken = mock(ValidateToken.class);
        User testUser = new User("test", "test@gmail.com", "1", false);
        when(validateToken.validateToken("")).thenReturn(testUser);

        EmailService emailService = new EmailService();
        var field = EmailService.class.getDeclaredField("validateToken");
        field.setAccessible(true);
        field.set(emailService, validateToken);


        var apiField = EmailService.class.getDeclaredField("sendGridApiKey");
        apiField.setAccessible(true);
        apiField.set(emailService, "FAKE_API_KEY");

        var fromField = EmailService.class.getDeclaredField("fromEmail");
        fromField.setAccessible(true);
        fromField.set(emailService, "noreply@test.com");

        try (MockedConstruction<SendGrid> mockedSendGrid = mockConstruction(SendGrid.class, (mock, context) -> {
            Response mockResponse = new Response(202, "Accepted", null);
            when(mock.api(any(Request.class))).thenReturn(mockResponse);
        })) {

            String result = emailService.sendCancelEmailForUser(
                    testUser,
                    "Unregister",
                    "<html><body>Hello QR</body></html>"
            );
            assertTrue(result.contains("Status: 202"));
        }
    }
}
