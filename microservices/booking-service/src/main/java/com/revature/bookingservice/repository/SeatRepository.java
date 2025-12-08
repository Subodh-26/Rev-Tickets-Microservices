package com.revature.bookingservice.repository;

import com.revature.bookingservice.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowIdOrderByRowLabelAscSeatNumberAsc(Long showId);
    List<Seat> findByShowIdAndIsAvailableTrue(Long showId);
    List<Seat> findByShowIdAndRowLabelAndSeatNumber(Long showId, String rowLabel, Integer seatNumber);
}
