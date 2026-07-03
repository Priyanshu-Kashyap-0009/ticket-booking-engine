package com.priyanshu.booking.repository;
import com.priyanshu.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookingRepository extends JpaRepository<Booking, Long> {

}