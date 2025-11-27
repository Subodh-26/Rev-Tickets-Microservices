package com.revature.revtickets.repository;

import com.revature.revtickets.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByBookingBookingId(Long bookingId);
    
    @Query("SELECT DATE(p.paymentDate), SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end AND p.paymentStatus = 'SUCCESS' GROUP BY DATE(p.paymentDate)")
    List<Object[]> findRevenueByDateRange(LocalDateTime start, LocalDateTime end);
}
