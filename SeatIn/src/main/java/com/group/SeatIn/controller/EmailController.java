package com.group.SeatIn.controller;

import com.group.SeatIn.model.Event;
import com.group.SeatIn.model.User;
import com.group.SeatIn.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-qr")
    /**
     * Sends an email containing a QR code to the specified recipient.
     *
     * @param request a map containing key-value pairs from the request body.
     *
     * @return a string message indicating the result of the operation,
     *         such as success or a failure message.
     */
    public String sendQrEmail(@RequestBody Map<String, String> request, @RequestHeader("Authorization") String token) {
        try {

            String base64 = request.get("qrBase64");

            String subject = "Your QR Code Ticket";


            String htmlBody = "<div style=\"max-width: 500px; margin: 0 auto; background-color: white; border-radius: 8px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">"
                    +

                    "<div style=\"text-align: center; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 2px solid #4f46e5;\">"
                    +
                    "<h1 style=\"color: #4f46e5; font-size: 32px; margin: 0; font-weight: bold;\">SeatIn</h1>" +
                    "<p style=\"color: #666; margin: 5px 0 0 0; font-size: 14px;\">Your Perfect Seat Awaits</p>" +
                    "</div>" +

                    "<div style=\"text-align: center;\">" +
                    "<p style=\"color: #666; font-size: 16px; margin: 0 0 25px 0;\">Please show the QR Code that has been sent as an attachment at the entrance and we hope you have a perfect event.</p>"
                    +

                    "</div>" +

                    "<div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; text-align: center;\">"
                    +
                    "<p style=\"color: #888; font-size: 14px; line-height: 1.5; margin: 0;\">" +
                    "Present this QR code at the venue entrance<br>" +
                    "Need help? Contact <a href=\"mailto:officialseatin@gmail.com\" style=\"color: #4f46e5;\">officialseatin@gmail.com</a>"
                    +
                    "</p>" +
                    "</div>" +
                    "</div>";

            return emailService.sendEmailWithQr(subject, htmlBody, base64, token);

        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
    @PostMapping("/send-unregister-event")
    /**
     * Sends an email containing details of unregistration.
     *
     * @param request a map containing key-value pairs from the request body.
     *
     * @return a string message indicating the result of the operation,
     *         such as success or a failure message.
     */
    public String sendUnregisteredEventEmail(@RequestBody Map<String, String> request, @RequestHeader("Authorization") String token) {
        try {

            String eventTitle = request.get("eventTitle");
            String cost = request.get("cost");
            String startDate = request.get("startDate");
            String location = request.get("location");
            String organiser = request.get("organiser");

            String subject = "Your Unregistration for " + eventTitle;


            String htmlBody =
                    "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; padding: 30px; font-family: Arial, sans-serif; box-shadow: 0 4px 12px rgba(0,0,0,0.1);\">" +

                            "<div style=\"text-align: center; margin-bottom: 30px; border-bottom: 3px solid #4f46e5; padding-bottom: 20px;\">" +
                            "<h1 style=\"color: #4f46e5; font-size: 32px; margin: 0;\">SeatIn</h1>" +
                            "<p style=\"color: #555555; margin: 5px 0 0; font-size: 14px;\">Your Perfect Seat Awaits</p>" +
                            "</div>" +

                            "<div style=\"text-align: left; margin-bottom: 30px;\">" +
                            "<h2 style=\"color: #333333; font-size: 22px; margin-bottom: 10px;\">You have successfully unregistered from the event</h2>" +
                            "<table style=\"width: 100%; border-collapse: collapse;\">" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Event:</td><td style=\"padding: 5px 0;\">" + eventTitle + "</td></tr>" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Date & Time:</td><td style=\"padding: 5px 0;\">" + startDate + "</td></tr>" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Location:</td><td style=\"padding: 5px 0;\">" + location + "</td></tr>" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Organiser:</td><td style=\"padding: 5px 0;\">" + organiser + "</td></tr>" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Refund Amount:</td><td style=\"padding: 5px 0;\">$" + cost + "</td></tr>" +
                            "</table>" +
                            "</div>" +

                            "<div style=\"text-align: center; border-top: 1px solid #eeeeee; padding-top: 20px; margin-top: 30px;\">" +
                            "<p style=\"color: #888888; font-size: 14px; line-height: 1.5; margin: 0;\">" +
                            "If you need help, contact us at " +
                            "<a href=\"mailto:officialseatin@gmail.com\" style=\"color: #4f46e5;\">officialseatin@gmail.com</a>." +
                            "</p>" +
                            "</div>" +

                            "</div>";



            return emailService.sendEmailForUnregisteringEvent(subject, htmlBody, token);

        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }


    public String sendEventCancelledEmailRequestBody (Event event, User user) {
        try {
            String eventTitle = event.getEventName();
            LocalDateTime startDate = event.getStart();
            String location = event.getLocation();
            String organiser = event.getOrganiser().getUsername();

            String subject = "Event Cancelled: " + eventTitle;

            String htmlBody =
                    "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; padding: 30px; font-family: Arial, sans-serif; box-shadow: 0 4px 12px rgba(0,0,0,0.1);\">" +

                            "<div style=\"text-align: center; margin-bottom: 30px; border-bottom: 3px solid #4f46e5; padding-bottom: 20px;\">" +
                            "<h1 style=\"color: #4f46e5; font-size: 32px; margin: 0;\">SeatIn</h1>" +
                            "<p style=\"color: #555555; margin: 5px 0 0; font-size: 14px;\">Your Perfect Seat Awaits</p>" +
                            "</div>" +

                            "<div style=\"text-align: left; margin-bottom: 30px;\">" +
                            "<h2 style=\"color: #333333; font-size: 22px; margin-bottom: 10px;\">This event has been cancelled</h2>" +
                            "<p style=\"color: #555555; font-size: 15px; margin-bottom: 20px;\">We’re sorry to inform you that the following event has been cancelled. A refund will be processed to your account shortly.</p>" +
                            "<table style=\"width: 100%; border-collapse: collapse;\">" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Event:</td><td style=\"padding: 5px 0;\">" + eventTitle + "</td></tr>" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Date & Time:</td><td style=\"padding: 5px 0;\">" + startDate + "</td></tr>" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Location:</td><td style=\"padding: 5px 0;\">" + location + "</td></tr>" +
                            "<tr><td style=\"padding: 5px 0; font-weight: bold;\">Organiser:</td><td style=\"padding: 5px 0;\">" + organiser + "</td></tr>" +
                            "</table>" +
                            "</div>" +

                            "<div style=\"text-align: center; border-top: 1px solid #eeeeee; padding-top: 20px; margin-top: 30px;\">" +
                            "<p style=\"color: #888888; font-size: 14px; line-height: 1.5; margin: 0;\">" +
                            "If you have any questions or need assistance, contact us at " +
                            "<a href=\"mailto:officialseatin@gmail.com\" style=\"color: #4f46e5;\">officialseatin@gmail.com</a>." +
                            "</p>" +
                            "</div>" +

                            "</div>";
            return emailService.sendCancelEmailForUser(user, subject, htmlBody);
    } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}