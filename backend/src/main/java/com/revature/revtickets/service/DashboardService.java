package com.revature.revtickets.service;

import com.revature.revtickets.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total counts
        stats.put("totalUsers", userRepository.count());
        stats.put("totalMovies", movieRepository.findByIsActiveTrue().size());
        stats.put("totalEvents", eventRepository.findByIsActiveTrue().size());
        stats.put("totalBookings", bookingRepository.count());

        // Today's stats
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        Long todayBookings = bookingRepository.countByBookingDateBetween(startOfDay, endOfDay);
        stats.put("todayBookings", todayBookings);

        // Revenue stats
        BigDecimal totalRevenue = paymentRepository.findAll().stream()
            .filter(p -> p.getPaymentStatus() == com.revature.revtickets.entity.Payment.PaymentStatus.SUCCESS)
            .map(p -> p.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalRevenue", totalRevenue);

        BigDecimal todayRevenue = paymentRepository.findAll().stream()
            .filter(p -> p.getPaymentStatus() == com.revature.revtickets.entity.Payment.PaymentStatus.SUCCESS)
            .filter(p -> p.getPaymentDate().isAfter(startOfDay) && p.getPaymentDate().isBefore(endOfDay))
            .map(p -> p.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("todayRevenue", todayRevenue);

        // Booking status breakdown
        Long confirmedBookings = bookingRepository.countByBookingStatus(com.revature.revtickets.entity.Booking.BookingStatus.CONFIRMED);
        Long pendingBookings = bookingRepository.countByBookingStatus(com.revature.revtickets.entity.Booking.BookingStatus.PENDING);
        Long cancelledBookings = bookingRepository.countByBookingStatus(com.revature.revtickets.entity.Booking.BookingStatus.CANCELLED);
        
        Map<String, Long> bookingStatusMap = new HashMap<>();
        bookingStatusMap.put("confirmed", confirmedBookings);
        bookingStatusMap.put("pending", pendingBookings);
        bookingStatusMap.put("cancelled", cancelledBookings);
        stats.put("bookingsByStatus", bookingStatusMap);

        // Upcoming shows count
        Long upcomingShows = showRepository.findByShowDateAfterAndIsActiveTrue(LocalDate.now()).stream().count();
        stats.put("upcomingShows", upcomingShows);

        return stats;
    }

    public List<Object[]> getRevenueByDate(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return paymentRepository.findRevenueByDateRange(start, end);
    }

    public List<Object[]> getTopMoviesByBookings(int limit) {
        return bookingRepository.findTopMoviesByBookings();
    }

    public List<Object[]> getTopEventsByBookings(int limit) {
        return bookingRepository.findTopEventsByBookings();
    }

    public Map<String, Long> getUserRegistrationStats(int days) {
        Map<String, Long> stats = new HashMap<>();
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        
        Long newUsers = userRepository.countByCreatedAtAfter(since);
        stats.put("newUsers", newUsers);
        stats.put("totalUsers", userRepository.count());
        
        return stats;
    }
}
