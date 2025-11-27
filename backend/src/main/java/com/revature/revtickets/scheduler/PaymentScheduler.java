package com.revature.revtickets.scheduler;

import com.revature.revtickets.entity.Booking;
import com.revature.revtickets.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Runs every minute to check for pending payments older than 5 minutes
     * and marks them as FAILED
     */
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void failExpiredPendingPayments() {
        try {
            // Calculate the cutoff time (5 minutes ago)
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
            
            // Find all bookings with PENDING payment status that are older than 5 minutes
            List<Booking> expiredBookings = bookingRepository.findAll().stream()
                    .filter(booking -> "PENDING".equals(booking.getPaymentStatus()))
                    .filter(booking -> booking.getBookingDate() != null)
                    .filter(booking -> booking.getBookingDate().isBefore(cutoffTime))
                    .toList();
            
            if (!expiredBookings.isEmpty()) {
                System.out.println("🕐 Found " + expiredBookings.size() + " expired pending payments. Marking as FAILED...");
                
                for (Booking booking : expiredBookings) {
                    booking.setPaymentStatus("FAILED");
                    booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                    System.out.println("   ❌ Booking " + booking.getBookingReference() + " marked as FAILED (pending for >5 minutes)");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in payment scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
