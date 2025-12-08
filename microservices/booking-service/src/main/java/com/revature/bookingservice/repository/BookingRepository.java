package com.revature.bookingservice.repository;

import com.revature.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);
    Optional<Booking> findByBookingReference(String reference);
    Long countByBookingStatus(Booking.BookingStatus status);
    
    @Query("SELECT DISTINCT b.showId FROM Booking b")
    List<Long> findDistinctShowIds();
}
