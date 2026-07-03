package com.priyanshu.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingConfirmedEvent {

    private Long bookingId;
    private String userEmail;
    private String userName;
    private String eventName;
    private String seatNumber;
}