package com.priyanshu.booking.service;

import com.priyanshu.booking.model.*;
import com.priyanshu.booking.repository.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class BookingService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private KafkaTemplate<String, BookingConfirmedEvent> kafkaTemplate;

    @Transactional
    public Booking bookSeat(Long userId, Long seatId) throws InterruptedException {

        String lockKey = "lock:seat:" + seatId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);

            if (!acquired) {
                throw new RuntimeException(
                        "Seat is busy, please try again in a moment");
            }

            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat is not available");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);

            Booking booking = new Booking();
            booking.setUser(user);
            booking.setSeat(seat);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setCreatedAt(LocalDateTime.now());
            Booking savedBooking = bookingRepository.save(booking);

            // publish to Kafka AFTER saving — async, non-blocking
            BookingConfirmedEvent event = new BookingConfirmedEvent(
                    savedBooking.getId(),
                    user.getEmail(),
                    user.getName(),
                    seat.getEvent().getName(),
                    seat.getSeatNumber()
            );
            kafkaTemplate.send("booking-events", event);

            return savedBooking;

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}