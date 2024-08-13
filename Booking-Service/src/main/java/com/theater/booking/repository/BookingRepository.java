package com.theater.booking.repository;

import com.theater.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    Optional<List<Booking>> findByUserMail(String userId);
    
}
