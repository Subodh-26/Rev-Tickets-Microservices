package com.revature.revtickets.controller;

import com.revature.revtickets.dto.EventDTO;
import com.revature.revtickets.entity.Event;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Event>>> getAllEvents() {
        List<Event> events = eventService.getAllActiveEvents();
        return ResponseEntity.ok(new ApiResponse<>(true, "Events retrieved successfully", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event retrieved successfully", event));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Event>>> getEventsByCategory(@PathVariable String category) {
        List<Event> events = eventService.getEventsByCategory(category);
        return ResponseEntity.ok(new ApiResponse<>(true, "Events by category retrieved", events));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<Event>>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(new ApiResponse<>(true, "Upcoming events retrieved", events));
    }
}
