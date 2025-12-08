package com.revature.venueservice.repository;

import com.revature.venueservice.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByIsActiveTrue();
    List<Venue> findByCityAndIsActiveTrue(String city);
}
