package com.priyanshu.booking.controller;

import com.priyanshu.booking.model.Booking;
import com.priyanshu.booking.service.BookingService;
import com.priyanshu.booking.service.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RateLimitService rateLimitService;

    @PostMapping
    public Booking createBooking(@RequestParam Long userId,
                                 @RequestParam Long seatId) throws InterruptedException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        if (!rateLimitService.isAllowed(email)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many requests. Slow down.");
        }

        return bookingService.bookSeat(userId, seatId);
    }
}