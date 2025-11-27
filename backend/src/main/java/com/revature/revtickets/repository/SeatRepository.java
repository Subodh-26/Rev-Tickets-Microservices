package com.revature.revtickets.repository;

import com.revature.revtickets.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowShowId(Long showId);
    List<Seat> findByShowShowIdOrderByRowLabelAscSeatNumberAsc(Long showId);
    List<Seat> findByShowShowIdAndIsAvailableTrue(Long showId);
    Long countByShowShowIdAndIsAvailableTrue(Long showId);
    Seat findByShowShowIdAndRowLabelAndSeatNumber(Long showId, String rowLabel, Integer seatNumber);
}
