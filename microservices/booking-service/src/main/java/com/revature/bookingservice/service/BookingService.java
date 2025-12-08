package com.revature.bookingservice.service;

import com.revature.bookingservice.entity.Booking;
import com.revature.bookingservice.repository.BookingRepository;
import com.revature.bookingservice.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    public List<java.util.Map<String, Object>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "bookingDate"));
        return bookings.stream().map(this::enrichBookingWithShow).collect(java.util.stream.Collectors.toList());
    }

    public List<java.util.Map<String, Object>> getUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserIdOrderByBookingDateDesc(userId);
        return bookings.stream().map(this::enrichBookingWithShow).collect(java.util.stream.Collectors.toList());
    }

    private java.util.Map<String, Object> enrichBookingWithShow(Booking booking) {
        java.util.Map<String, Object> enriched = new java.util.HashMap<>();
        enriched.put("bookingId", booking.getBookingId());
        enriched.put("userId", booking.getUserId());
        enriched.put("showId", booking.getShowId());
        enriched.put("bookingReference", booking.getBookingReference());
        enriched.put("totalSeats", booking.getTotalSeats());
        enriched.put("totalAmount", booking.getTotalAmount());
        enriched.put("bookingStatus", booking.getBookingStatus().toString());
        enriched.put("paymentStatus", booking.getPaymentStatus());
        enriched.put("bookingDate", booking.getBookingDate());
        enriched.put("isCancelled", booking.getBookingStatus() == Booking.BookingStatus.CANCELLED);
        
        // Add show details
        if (booking.getShowId() != null) {
            showRepository.findById(booking.getShowId()).ifPresent(show -> {
                java.util.Map<String, Object> showData = new java.util.HashMap<>();
                showData.put("showDate", show.getShowDate());
                showData.put("showTime", show.getShowTime());
                showData.put("basePrice", show.getBasePrice());
                enriched.put("show", showData);
            });
        }
        
        return enriched;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.revature.bookingservice.repository.SeatRepository seatRepository;

    public java.util.Map<String, Object> getBookingStats() {
        List<Booking> allBookings = bookingRepository.findAll();
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        java.math.BigDecimal totalRevenue = allBookings.stream()
            .filter(b -> b.getBookingStatus() == Booking.BookingStatus.CONFIRMED)
            .map(Booking::getTotalAmount)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        long confirmedCount = allBookings.stream()
            .filter(b -> b.getBookingStatus() == Booking.BookingStatus.CONFIRMED)
            .count();
        
        long pendingCount = allBookings.stream()
            .filter(b -> b.getBookingStatus() == Booking.BookingStatus.PENDING)
            .count();
        
        long cancelledCount = allBookings.stream()
            .filter(b -> b.getBookingStatus() == Booking.BookingStatus.CANCELLED)
            .count();
        
        long incompletePaymentCount = allBookings.stream()
            .filter(b -> !"COMPLETED".equals(b.getPaymentStatus()))
            .count();
        
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalBookings", allBookings.size());
        stats.put("cancelledPayments", incompletePaymentCount);
        
        java.util.Map<String, Long> bookingsByStatus = new java.util.HashMap<>();
        bookingsByStatus.put("confirmed", confirmedCount);
        bookingsByStatus.put("pending", pendingCount);
        bookingsByStatus.put("cancelled", cancelledCount);
        stats.put("bookingsByStatus", bookingsByStatus);
        
        return stats;
    }

    public Booking createBooking(java.util.Map<String, Object> bookingData) {
        Booking booking = new Booking();
        booking.setUserId(Long.parseLong(bookingData.get("userId").toString()));
        Long showId = Long.parseLong(bookingData.get("showId").toString());
        booking.setShowId(showId);
        booking.setTotalAmount(new java.math.BigDecimal(bookingData.get("totalAmount").toString()));
        booking.setBookingReference("BK" + System.currentTimeMillis());
        
        // Get seat numbers and lock them
        Object seatNumbersObj = bookingData.get("seatNumbers");
        int totalSeats = 1;
        if (seatNumbersObj instanceof java.util.List) {
            java.util.List<?> seatNumbers = (java.util.List<?>) seatNumbersObj;
            totalSeats = seatNumbers.size();
            final int seatsBooked = totalSeats;
            
            // Lock each seat
            for (Object seatLabel : seatNumbers) {
                String seatStr = seatLabel.toString();
                String rowLabel = seatStr.replaceAll("\\d", "");
                Integer seatNumber = Integer.parseInt(seatStr.replaceAll("\\D", ""));
                
                // Find and lock the seat
                java.util.List<com.revature.bookingservice.entity.Seat> seats = 
                    seatRepository.findByShowIdAndRowLabelAndSeatNumber(showId, rowLabel, seatNumber);
                if (!seats.isEmpty()) {
                    com.revature.bookingservice.entity.Seat seat = seats.get(0);
                    seat.setIsAvailable(false);
                    seatRepository.save(seat);
                }
            }
            
            // Update show available seats
            showRepository.findById(showId).ifPresent(show -> {
                show.setAvailableSeats(show.getAvailableSeats() - seatsBooked);
                showRepository.save(show);
            });
        }
        booking.setTotalSeats(totalSeats);
        
        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPaymentStatus("COMPLETED");
        return bookingRepository.save(booking);
    }
}
