package com.revature.revtickets.repository;

import com.revature.revtickets.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByVenue_VenueIdAndIsActiveTrue(Long venueId);
    List<Screen> findByVenue_VenueId(Long venueId);
}
