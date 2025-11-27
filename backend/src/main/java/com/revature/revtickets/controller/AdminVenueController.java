package com.revature.revtickets.controller;

import com.revature.revtickets.dto.ScreenDTO;
import com.revature.revtickets.dto.VenueDTO;
import com.revature.revtickets.entity.Screen;
import com.revature.revtickets.entity.Venue;
import com.revature.revtickets.repository.ScreenRepository;
import com.revature.revtickets.repository.VenueRepository;
import com.revature.revtickets.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/venues")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminVenueController {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VenueDTO>>> getAllVenues() {
        try {
            List<Venue> venues = venueRepository.findAll();
            List<VenueDTO> venueDTOs = venues.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>(true, "Venues fetched successfully", venueDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch venues: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VenueDTO>> getVenueById(@PathVariable Long id) {
        try {
            Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Venue fetched successfully", convertToDTO(venue)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch venue: " + e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VenueDTO>> createVenue(@RequestBody VenueDTO venueDTO) {
        try {
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

            // Save screens
            if (venueDTO.getScreens() != null && !venueDTO.getScreens().isEmpty()) {
                for (ScreenDTO screenDTO : venueDTO.getScreens()) {
                    Screen screen = new Screen();
                    screen.setVenue(savedVenue);
                    screen.setScreenNumber(screenDTO.getScreenNumber());
                    screen.setScreenType(screenDTO.getScreenType());
                    screen.setSoundSystem(screenDTO.getSoundSystem());
                    screen.setSeatLayout(screenDTO.getSeatLayout());
                    
                    // Extract totalSeats from seatLayout if not provided
                    Integer totalSeats = screenDTO.getTotalSeats();
                    if (totalSeats == null && screenDTO.getSeatLayout() != null) {
                        Object totalSeatsObj = screenDTO.getSeatLayout().get("totalSeats");
                        if (totalSeatsObj != null) {
                            totalSeats = Integer.parseInt(totalSeatsObj.toString());
                        }
                    }
                    screen.setTotalSeats(totalSeats);
                    screen.setIsActive(true);
                    screenRepository.save(screen);
                }
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Venue created successfully", convertToDTO(savedVenue)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to create venue: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VenueDTO>> updateVenue(@PathVariable Long id, @RequestBody VenueDTO venueDTO) {
        try {
            Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
            
            venue.setVenueName(venueDTO.getName());
            venue.setAddress(venueDTO.getAddress());
            venue.setCity(venueDTO.getCity());
            venue.setState(venueDTO.getState());
            venue.setPincode(venueDTO.getPincode());
            venue.setTotalScreens(venueDTO.getTotalScreens());
            venue.setFacilities(venueDTO.getFacilities());
            
            Venue savedVenue = venueRepository.save(venue);

            // Delete existing screens and create new ones
            List<Screen> existingScreens = screenRepository.findByVenue_VenueId(id);
            screenRepository.deleteAll(existingScreens);

            if (venueDTO.getScreens() != null && !venueDTO.getScreens().isEmpty()) {
                for (ScreenDTO screenDTO : venueDTO.getScreens()) {
                    Screen screen = new Screen();
                    screen.setVenue(savedVenue);
                    screen.setScreenNumber(screenDTO.getScreenNumber());
                    screen.setScreenType(screenDTO.getScreenType());
                    screen.setSoundSystem(screenDTO.getSoundSystem());
                    screen.setSeatLayout(screenDTO.getSeatLayout());
                    
                    // Extract totalSeats from seatLayout if not provided
                    Integer totalSeats = screenDTO.getTotalSeats();
                    if (totalSeats == null && screenDTO.getSeatLayout() != null) {
                        Object totalSeatsObj = screenDTO.getSeatLayout().get("totalSeats");
                        if (totalSeatsObj != null) {
                            totalSeats = Integer.parseInt(totalSeatsObj.toString());
                        }
                    }
                    screen.setTotalSeats(totalSeats);
                    screen.setIsActive(true);
                    screenRepository.save(screen);
                }
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Venue updated successfully", convertToDTO(savedVenue)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to update venue: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVenue(@PathVariable Long id) {
        try {
            Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
            
            // Delete associated screens
            List<Screen> screens = screenRepository.findByVenue_VenueId(id);
            screenRepository.deleteAll(screens);
            
            venueRepository.delete(venue);
            return ResponseEntity.ok(new ApiResponse<>(true, "Venue deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to delete venue: " + e.getMessage(), null));
        }
    }

    private VenueDTO convertToDTO(Venue venue) {
        List<Screen> screens = screenRepository.findByVenue_VenueId(venue.getVenueId());
        List<ScreenDTO> screenDTOs = screens.stream().map(screen -> ScreenDTO.builder()
            .id(screen.getScreenId())
            .screenNumber(screen.getScreenNumber())
            .screenType(screen.getScreenType())
            .soundSystem(screen.getSoundSystem())
            .seatLayout(screen.getSeatLayout())
            .totalSeats(screen.getTotalSeats())
            .build()).collect(Collectors.toList());

        return VenueDTO.builder()
            .id(venue.getVenueId())
            .name(venue.getVenueName())
            .address(venue.getAddress())
            .city(venue.getCity())
            .state(venue.getState())
            .pincode(venue.getPincode())
            .totalScreens(venue.getTotalScreens())
            .facilities(venue.getFacilities())
            .screens(screenDTOs)
            .build();
    }
}
