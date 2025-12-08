package com.revature.venueservice.service;

import com.revature.venueservice.dto.VenueDTO;
import com.revature.venueservice.entity.Venue;
import com.revature.venueservice.entity.Screen;
import com.revature.venueservice.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;
    
    @Autowired
    private ScreenService screenService;

    public List<Venue> getAllActiveVenues() {
        return venueRepository.findByIsActiveTrue();
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venue not found"));
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

    @Transactional
    public Venue createVenueWithScreens(VenueDTO venueDTO) {
        Venue venue = new Venue();
        venue.setVenueName(venueDTO.getName());
        venue.setAddress(venueDTO.getAddress());
        venue.setCity(venueDTO.getCity());
        venue.setState(venueDTO.getState());
        venue.setPincode(venueDTO.getPincode());
        venue.setTotalScreens(venueDTO.getTotalScreens());
        venue.setFacilities(venueDTO.getFacilities());
        venue.setIsActive(true);
        
        Venue savedVenue = venueRepository.save(venue);
        
        // Create screens if provided
        if (venueDTO.getScreens() != null && !venueDTO.getScreens().isEmpty()) {
            try {
                List<Screen> screens = venueDTO.getScreens().stream()
                    .map(screenDTO -> {
                        Screen screen = new Screen();
                        screen.setVenueId(savedVenue.getVenueId());
                        screen.setScreenNumber(screenDTO.getScreenNumber());
                        screen.setScreenType(screenDTO.getScreenType());
                        screen.setSoundSystem(screenDTO.getSoundSystem());
                        screen.setSeatLayout(screenDTO.getSeatLayout());
                        screen.setTotalSeats(screenDTO.getTotalSeats());
                        screen.setIsActive(true);
                        return screen;
                    })
                    .collect(java.util.stream.Collectors.toList());
                screenService.saveScreens(screens);
            } catch (Exception e) {
                System.err.println("Error creating screens: " + e.getMessage());
            }
        }
        
        return savedVenue;
    }

    @Transactional
    public Venue updateVenueWithScreens(Long id, VenueDTO venueDTO) {
        Venue venue = getVenueById(id);
        
        venue.setVenueName(venueDTO.getName());
        venue.setAddress(venueDTO.getAddress());
        venue.setCity(venueDTO.getCity());
        venue.setState(venueDTO.getState());
        venue.setPincode(venueDTO.getPincode());
        venue.setTotalScreens(venueDTO.getTotalScreens());
        venue.setFacilities(venueDTO.getFacilities());
        
        Venue savedVenue = venueRepository.save(venue);
        
        // Update screens if provided
        if (venueDTO.getScreens() != null) {
            try {
                // Delete existing screens
                screenService.deleteScreensByVenueId(id);
                
                // Create new screens
                if (!venueDTO.getScreens().isEmpty()) {
                    List<Screen> screens = venueDTO.getScreens().stream()
                        .map(screenDTO -> {
                            Screen screen = new Screen();
                            screen.setVenueId(savedVenue.getVenueId());
                            screen.setScreenNumber(screenDTO.getScreenNumber());
                            screen.setScreenType(screenDTO.getScreenType());
                            screen.setSoundSystem(screenDTO.getSoundSystem());
                            screen.setSeatLayout(screenDTO.getSeatLayout());
                            screen.setTotalSeats(screenDTO.getTotalSeats());
                            screen.setIsActive(true);
                            return screen;
                        })
                        .collect(java.util.stream.Collectors.toList());
                    screenService.saveScreens(screens);
                }
            } catch (Exception e) {
                System.err.println("Error updating screens: " + e.getMessage());
            }
        }
        
        return savedVenue;
    }

    @Transactional
    public void hardDeleteVenue(Long id) {
        try {
            // Delete screens first
            screenService.deleteScreensByVenueId(id);
        } catch (Exception e) {
            System.err.println("Error deleting screens: " + e.getMessage());
        }
        // Delete venue
        venueRepository.deleteById(id);
    }
}