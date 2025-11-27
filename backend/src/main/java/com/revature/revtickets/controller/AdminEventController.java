package com.revature.revtickets.controller;

import com.revature.revtickets.entity.Event;
import com.revature.revtickets.repository.EventRepository;
import com.revature.revtickets.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/events")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminEventController {

    @Autowired
    private EventRepository eventRepository;

    private static final String DISPLAY_IMAGE_DIRECTORY = "backend/public/display/";
    private static final String BANNER_IMAGE_DIRECTORY = "backend/public/banner/";

    @GetMapping
    public ResponseEntity<ApiResponse<List<Event>>> getAllEvents(@RequestParam(defaultValue = "true") Boolean activeOnly) {
        try {
            List<Event> events = activeOnly ? 
                eventRepository.findByIsActiveTrue() : 
                eventRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Events fetched successfully", events));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch events: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getEventById(@PathVariable Long id) {
        try {
            Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Event fetched successfully", event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch event: " + e.getMessage(), null));
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Event>> createEvent(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String artist,
            @RequestParam String ageRestriction,
            @RequestParam Integer duration,
            @RequestParam String eventDate,
            @RequestParam(required = false) String eventTime,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) MultipartFile displayImage,
            @RequestParam(required = false) MultipartFile bannerImage) {
        try {
            Event event = new Event();
            event.setTitle(title);
            event.setDescription(description);
            event.setCategory(category);
            event.setArtistOrTeam(artist);
            event.setAgeRestriction(ageRestriction);
            event.setDurationMinutes(duration);
            event.setEventDate(LocalDate.parse(eventDate));
            if (eventTime != null && !eventTime.isEmpty()) {
                event.setEventTime(LocalTime.parse(eventTime));
            }
            if (language != null && !language.isEmpty()) {
                event.setLanguage(language);
            }
            event.setIsActive(true);

            if (displayImage != null && !displayImage.isEmpty()) {
                String filename = saveImage(displayImage, DISPLAY_IMAGE_DIRECTORY);
                event.setDisplayImageUrl("/display/" + filename);
            }

            if (bannerImage != null && !bannerImage.isEmpty()) {
                String filename = saveImage(bannerImage, BANNER_IMAGE_DIRECTORY);
                event.setBannerImageUrl("/banner/" + filename);
            }

            Event savedEvent = eventRepository.save(event);
            return ResponseEntity.ok(new ApiResponse<>(true, "Event created successfully", savedEvent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to create event: " + e.getMessage(), null));
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Event>> updateEvent(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String artist,
            @RequestParam String ageRestriction,
            @RequestParam Integer duration,
            @RequestParam String eventDate,
            @RequestParam(required = false) String eventTime,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) MultipartFile displayImage,
            @RequestParam(required = false) MultipartFile bannerImage) {
        try {
            Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            event.setTitle(title);
            event.setDescription(description);
            event.setCategory(category);
            event.setArtistOrTeam(artist);
            event.setAgeRestriction(ageRestriction);
            event.setDurationMinutes(duration);
            event.setEventDate(LocalDate.parse(eventDate));
            if (eventTime != null && !eventTime.isEmpty()) {
                event.setEventTime(LocalTime.parse(eventTime));
            }
            if (language != null && !language.isEmpty()) {
                event.setLanguage(language);
            }

            if (displayImage != null && !displayImage.isEmpty()) {
                // Delete old display image if exists
                if (event.getDisplayImageUrl() != null) {
                    deleteImage(event.getDisplayImageUrl(), DISPLAY_IMAGE_DIRECTORY);
                }
                String filename = saveImage(displayImage, DISPLAY_IMAGE_DIRECTORY);
                event.setDisplayImageUrl("/display/" + filename);
            }

            if (bannerImage != null && !bannerImage.isEmpty()) {
                // Delete old banner image if exists
                if (event.getBannerImageUrl() != null) {
                    deleteImage(event.getBannerImageUrl(), BANNER_IMAGE_DIRECTORY);
                }
                String filename = saveImage(bannerImage, BANNER_IMAGE_DIRECTORY);
                event.setBannerImageUrl("/banner/" + filename);
            }

            Event savedEvent = eventRepository.save(event);
            return ResponseEntity.ok(new ApiResponse<>(true, "Event updated successfully", savedEvent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to update event: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        try {
            Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            // Soft delete - just set isActive to false
            event.setIsActive(false);
            eventRepository.save(event);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Event deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to deactivate event: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Event>> activateEvent(@PathVariable Long id) {
        try {
            Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
            
            event.setIsActive(true);
            Event savedEvent = eventRepository.save(event);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Event activated successfully", savedEvent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to activate event: " + e.getMessage(), null));
        }
    }

    private String saveImage(MultipartFile file, String directory) throws Exception {
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(directory);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        return filename;
    }

    private void deleteImage(String imagePath, String directory) {
        try {
            // Extract filename from path like "/display/filename.jpg" or "/banner/filename.jpg"
            String filename = imagePath.substring(imagePath.lastIndexOf('/') + 1);
            Path filePath = Paths.get(directory + filename);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to delete image: " + e.getMessage());
        }
    }
}
