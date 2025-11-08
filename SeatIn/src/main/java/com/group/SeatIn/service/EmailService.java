package com.group.SeatIn.service;

import com.group.SeatIn.model.User;
import com.group.SeatIn.repository.UserRepository;
import com.group.SeatIn.security.JwtUtil;
import com.group.SeatIn.security.ValidateToken;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${spring.sendgrid.api-key}") // Get the api key from applications [DO NOT CHANGE]
    private String sendGridApiKey;

    @Value("${spring.mail.username}") // DO NOT CHANGE
    private String fromEmail;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidateToken validateToken;

    /**
     * Sends an HTML email with an embedded QR code image to the specified
     * recipient.
     *
     * @param token       access token of the user
     * @param subject     the subject line of the email
     * @param htmlContent the HTML content of the email body; can include an inline
     *                    image reference
     * @param base64Qr    a base64-encoded string representing the QR code image
     *
     * @return a string message indicating the result of the email sending operation
     *
     * @throws IOException if an error occurs while decoding the base64 image or
     *                     sending the email
     */
    public String sendEmailWithQr(String subject, String htmlContent, String base64Qr, String token)
            throws IOException {

        User user = validateToken.validateToken(token);
        String email = user.getEmail();
        Email from = new Email(fromEmail); // DO NOT CHANGE
        Email to = new Email(email);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        // Create inline attachment
        Attachments attachment = new Attachments();
        attachment.setContent(base64Qr);
        attachment.setType("image/png");
        attachment.setFilename("qrcode.png");
        attachment.setDisposition("inline");
        attachment.setContentId("qrcode");
        mail.addAttachments(attachment);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Status: " + response.getStatusCode() + "\nBody: " + response.getBody());
            return "Status: " + response.getStatusCode() + "\nBody: " + response.getBody();
        } catch (IOException e) {
            throw new IOException("Error sending email: " + e.getMessage(), e);
        }
    }

    public String sendEmailForUnregisteringEvent(String subject, String htmlContent, String token) throws IOException {
        User user = validateToken.validateToken(token);
        String email = user.getEmail();
        Email from = new Email(fromEmail); // DO NOT CHANGE
        Email to = new Email(email);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Status: " + response.getStatusCode() + "\nBody: " + response.getBody());
            return "Status: " + response.getStatusCode() + "\nBody: " + response.getBody();
        } catch (IOException e) {
            throw new IOException("Error sending email: " + e.getMessage(), e);
        }
    }
    public String sendCancelEmailForUser(User user, String subject, String htmlContent) throws IOException {
        String email = user.getEmail();
        Email from = new Email(fromEmail); // DO NOT CHANGE
        Email to = new Email(email);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Status: " + response.getStatusCode() + "\nBody: " + response.getBody());
            return "Status: " + response.getStatusCode() + "\nBody: " + response.getBody();
        } catch (IOException e) {
            throw new IOException("Error sending email: " + e.getMessage(), e);
        }
    }
}