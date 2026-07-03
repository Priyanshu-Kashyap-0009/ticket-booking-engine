package com.priyanshu.booking.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyanshu.booking.model.BookingConfirmedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private JavaMailSender mailSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "booking-events",
            groupId = "booking-group"
    )
    public void handleBookingConfirmed(byte[] messageBytes) {
        try {
            BookingConfirmedEvent event = objectMapper.readValue(
                    messageBytes, BookingConfirmedEvent.class);

            System.out.println("Received booking event for: "
                    + event.getUserEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@ticketbooking.com");
            message.setTo(event.getUserEmail());
            message.setSubject("Booking Confirmed - " + event.getEventName());
            message.setText(
                    "Hi " + event.getUserName() + ",\n\n" +
                            "Your booking is confirmed!\n\n" +
                            "Event: " + event.getEventName() + "\n" +
                            "Seat: " + event.getSeatNumber() + "\n" +
                            "Booking ID: " + event.getBookingId() + "\n\n" +
                            "Thank you for booking with us!"
            );

            mailSender.send(message);
            System.out.println("Email sent to: " + event.getUserEmail());

        } catch (Exception e) {
            System.err.println("Consumer error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}