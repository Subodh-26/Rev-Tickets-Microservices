package com.revature.movieservice.repository;

import com.revature.movieservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIsActiveTrue();
    List<Event> findByCategoryAndIsActiveTrue(String category);
    List<Event> findByEventDateGreaterThanEqualAndIsActiveTrue(LocalDate date);
}
