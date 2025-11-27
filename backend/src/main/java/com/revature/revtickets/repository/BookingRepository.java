package com.revature.revtickets.repository;

import com.revature.revtickets.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserUserIdOrderByBookingDateDesc(Long userId);
    Optional<Booking> findByBookingReference(String bookingReference);
    Optional<Booking> findByRazorpayOrderId(String razorpayOrderId);
    Long countByBookingDateBetween(LocalDateTime start, LocalDateTime end);
    Long countByBookingStatus(Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingDate >= :startDate AND b.bookingDate <= :endDate")
    List<Booking> findBookingsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT m.title, COUNT(b) FROM Booking b JOIN b.show s JOIN s.movie m WHERE s IS NOT NULL GROUP BY m.id ORDER BY COUNT(b) DESC")
    List<Object[]> findTopMoviesByBookings();
    
    @Query("SELECT e.title, COUNT(b) FROM Booking b JOIN b.show s JOIN s.event e WHERE s IS NOT NULL GROUP BY e.id ORDER BY COUNT(b) DESC")
    List<Object[]> findTopEventsByBookings();
}
