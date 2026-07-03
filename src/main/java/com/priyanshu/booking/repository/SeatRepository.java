package com.priyanshu.booking.repository;
import com.priyanshu.booking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SeatRepository extends JpaRepository<Seat, Long> {


}