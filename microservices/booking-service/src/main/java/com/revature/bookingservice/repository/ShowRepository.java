package com.revature.bookingservice.repository;

import com.revature.bookingservice.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByIsActiveTrueOrderByShowDateAscShowTimeAsc();
    List<Show> findByMovieIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(Long movieId);
    List<Show> findByEventIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(Long eventId);
    List<Show> findByVenueIdAndIsActiveTrueOrderByShowDateAscShowTimeAsc(Long venueId);
    
    @Query("SELECT DISTINCT s.movieId FROM Show s WHERE s.movieId IS NOT NULL AND s.isActive = true")
    List<Long> findDistinctMovieIds();
    
    @Query("SELECT DISTINCT s.eventId FROM Show s WHERE s.eventId IS NOT NULL AND s.isActive = true")
    List<Long> findDistinctEventIds();
}