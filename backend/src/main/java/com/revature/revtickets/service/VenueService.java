package com.revature.revtickets.service;

import com.revature.revtickets.entity.Venue;
import com.revature.revtickets.exception.ResourceNotFoundException;
import com.revature.revtickets.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;

    public List<Venue> getAllActiveVenues() {
        return venueRepository.findByIsActiveTrue();
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
    }

    public List<Venue> getVenuesByCity(String city) {
        return venueRepository.findByCityAndIsActiveTrue(city);
    }

    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    public Venue updateVenue(Long id, Venue venueDetails) {
        Venue venue = getVenueById(id);
        
        venue.setVenueName(venueDetails.getVenueName());
        venue.setAddress(venueDetails.getAddress());
        venue.setCity(venueDetails.getCity());
        venue.setState(venueDetails.getState());
        venue.setPincode(venueDetails.getPincode());
        venue.setTotalScreens(venueDetails.getTotalScreens());
        venue.setFacilities(venueDetails.getFacilities());
        
        return venueRepository.save(venue);
    }

    public void deleteVenue(Long id) {
        Venue venue = getVenueById(id);
        venue.setIsActive(false);
        venueRepository.save(venue);
    }
}
