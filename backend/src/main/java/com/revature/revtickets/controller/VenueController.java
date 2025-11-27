package com.revature.revtickets.controller;

import com.revature.revtickets.dto.ScreenDTO;
import com.revature.revtickets.dto.VenueDTO;
import com.revature.revtickets.entity.Screen;
import com.revature.revtickets.entity.Venue;
import com.revature.revtickets.repository.ScreenRepository;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/venues")
@CrossOrigin(origins = "*")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @Autowired
    private ScreenRepository screenRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VenueDTO>>> getAllVenues() {
        List<Venue> venues = venueService.getAllActiveVenues();
        List<VenueDTO> venueDTOs = venues.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Venues retrieved successfully", venueDTOs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VenueDTO>> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.getVenueById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Venue retrieved successfully", convertToDTO(venue)));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<VenueDTO>>> getVenuesByCity(@PathVariable String city) {
        List<Venue> venues = venueService.getVenuesByCity(city);
        List<VenueDTO> venueDTOs = venues.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Venues by city retrieved", venueDTOs));
    }

    private VenueDTO convertToDTO(Venue venue) {
        List<Screen> screens = screenRepository.findByVenue_VenueIdAndIsActiveTrue(venue.getVenueId());
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
