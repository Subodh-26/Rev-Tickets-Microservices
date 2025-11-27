package com.revature.revtickets.repository;

import com.revature.revtickets.entity.SeatLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatLayoutRepository extends JpaRepository<SeatLayout, Long> {
}
