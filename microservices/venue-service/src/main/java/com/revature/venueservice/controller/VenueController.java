package com.revature.venueservice.controller;

import com.revature.venueservice.dto.ApiResponse;
import com.revature.venueservice.dto.VenueDTO;
import com.revature.venueservice.dto.ScreenDTO;
import com.revature.venueservice.entity.Venue;
import com.revature.venueservice.entity.Screen;
import com.revature.venueservice.service.VenueService;
import com.revature.venueservice.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;
    
    @Autowired
    private ScreenService screenService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Venue>>> getAllVenues() {
        try {
            List<Venue> venues = venueService.getAllActiveVenues();
            return ResponseEntity.ok(new ApiResponse<>(true, "Venues retrieved successfully", venues));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Venue>> getVenueById(@PathVariable Long id) {
        try {
            Venue venue = venueService.getVenueById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Venue retrieved successfully", venue));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<Venue>>> getVenuesByCity(@PathVariable String city) {
        try {
            List<Venue> venues = venueService.getVenuesByCity(city);
            return ResponseEntity.ok(new ApiResponse<>(true, "Venues by city retrieved", venues));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<VenueDTO>>> getAdminVenues() {
        try {
            List<Venue> venues = venueService.getAllVenues();
            List<VenueDTO> venueDTOs = venues.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>(true, "Venues retrieved successfully", venueDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<VenueDTO>> getAdminVenueById(@PathVariable Long id) {
        try {
            Venue venue = venueService.getVenueById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Venue retrieved successfully", convertToDTO(venue)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<VenueDTO>> createAdminVenue(@RequestBody VenueDTO venueDTO) {
        try {
            Venue savedVenue = venueService.createVenueWithScreens(venueDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Venue created successfully", convertToDTO(savedVenue)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<VenueDTO>> updateAdminVenue(@PathVariable Long id, @RequestBody VenueDTO venueDTO) {
        try {
            Venue savedVenue = venueService.updateVenueWithScreens(id, venueDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Venue updated successfully", convertToDTO(savedVenue)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdminVenue(@PathVariable Long id) {
        try {
            venueService.hardDeleteVenue(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Venue deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    private VenueDTO convertToDTO(Venue venue) {
        VenueDTO dto = new VenueDTO();
        dto.setId(venue.getVenueId());
        dto.setName(venue.getVenueName());
        dto.setAddress(venue.getAddress());
        dto.setCity(venue.getCity());
        dto.setState(venue.getState());
        dto.setPincode(venue.getPincode());
        dto.setTotalScreens(venue.getTotalScreens());
        dto.setFacilities(venue.getFacilities());
        
        try {
            List<Screen> screens = screenService.getScreensByVenueId(venue.getVenueId());
            dto.setScreens(screenService.convertToScreenDTOs(screens));
        } catch (Exception e) {
            dto.setScreens(java.util.Collections.emptyList());
        }
        
        return dto;
    }
}