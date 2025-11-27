package com.revature.revtickets.controller;

import com.revature.revtickets.dto.ShowDTO;
import com.revature.revtickets.entity.Show;
import com.revature.revtickets.entity.OpenEventShow;
import com.revature.revtickets.entity.Screen;
import com.revature.revtickets.entity.Seat;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.ShowService;
import com.revature.revtickets.service.OpenEventShowService;
import com.revature.revtickets.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "*")
public class ShowController {

    @Autowired
    private ShowService showService;
    
    @Autowired
    private OpenEventShowService openEventShowService;

    @Autowired
    private SeatService seatService;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<Show>>> getShowsByMovie(
            @PathVariable Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Show> shows = showService.getShowsByMovieId(movieId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shows retrieved successfully", shows));
    }
    
    @GetMapping("/movie/{movieId}/dates")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getAvailableDatesForMovie(@PathVariable Long movieId) {
        List<LocalDate> dates = showService.getAvailableDatesForMovie(movieId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available dates retrieved successfully", dates));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShowsByEvent(
            @PathVariable Long eventId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Show> regularShows = showService.getShowsByEventId(eventId, date);
        List<OpenEventShow> openShows = openEventShowService.getShowsByEventIdAndDate(eventId, date);
        
        Map<String, Object> response = new HashMap<>();
        response.put("regularShows", regularShows);
        response.put("openEventShows", openShows);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Shows retrieved successfully", response));
    }
    
    @GetMapping("/event/{eventId}/dates")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getAvailableDatesForEvent(@PathVariable Long eventId) {
        List<LocalDate> regularDates = showService.getAvailableDatesForEvent(eventId);
        List<LocalDate> openDates = openEventShowService.getAvailableDatesForEvent(eventId);
        
        // Combine and deduplicate dates
        List<LocalDate> allDates = new ArrayList<>(regularDates);
        for (LocalDate date : openDates) {
            if (!allDates.contains(date)) {
                allDates.add(date);
            }
        }
        allDates.sort(LocalDate::compareTo);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Available dates retrieved successfully", allDates));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Show>> getShowById(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Show retrieved successfully", show));
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<ApiResponse<List<Show>>> getShowsByVenue(
            @PathVariable Long venueId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Show> shows = showService.getShowsByVenueId(venueId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shows by venue retrieved", shows));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<Show>>> getUpcomingShows() {
        List<Show> shows = showService.getUpcomingShows();
        return ResponseEntity.ok(new ApiResponse<>(true, "Upcoming shows retrieved", shows));
    }

    @GetMapping("/open-event-shows/{id}")
    public ResponseEntity<ApiResponse<OpenEventShow>> getOpenEventShowById(@PathVariable Long id) {
        OpenEventShow show = openEventShowService.getShowById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Open event show retrieved successfully", show));
    }

    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<ApiResponse<Show>> softDeleteShow(@PathVariable Long id) {
        Show show = showService.softDeleteShow(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Show soft deleted successfully", show));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Show>> activateShow(@PathVariable Long id) {
        Show show = showService.activateShow(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Show activated successfully", show));
    }

    @PutMapping("/open-event-shows/{id}/soft-delete")
    public ResponseEntity<ApiResponse<OpenEventShow>> softDeleteOpenEventShow(@PathVariable Long id) {
        OpenEventShow show = openEventShowService.softDeleteShow(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Open event show soft deleted successfully", show));
    }

    @PutMapping("/open-event-shows/{id}/activate")
    public ResponseEntity<ApiResponse<OpenEventShow>> activateOpenEventShow(@PathVariable Long id) {
        OpenEventShow show = openEventShowService.activateShow(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Open event show activated successfully", show));
    }

    @PostMapping("/{id}/generate-seats")
    public ResponseEntity<ApiResponse<List<Seat>>> generateSeatsForShow(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        Screen screen = show.getScreen();
        
        // Parse actual seat layout from screen
        int totalRows = 10; // default
        int seatsPerRow = 26; // default
        
        try {
            Map<String, Object> seatLayoutMap = screen.getSeatLayout();
            if (seatLayoutMap != null && !seatLayoutMap.isEmpty()) {
                Object rowsObj = seatLayoutMap.get("rows");
                if (rowsObj instanceof List) {
                    totalRows = ((List<?>) rowsObj).size();
                }
                
                Object seatsPerRowObj = seatLayoutMap.get("seatsPerRow");
                if (seatsPerRowObj instanceof Map) {
                    Map<?, ?> seatsPerRowMap = (Map<?, ?>) seatsPerRowObj;
                    if (!seatsPerRowMap.isEmpty()) {
                        Object firstRowSeats = seatsPerRowMap.values().iterator().next();
                        if (firstRowSeats instanceof Number) {
                            seatsPerRow = ((Number) firstRowSeats).intValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing seat layout, using defaults: " + e.getMessage());
        }
        
        List<Seat> seats = seatService.generateSeatsForShow(id, totalRows, seatsPerRow);
        return ResponseEntity.ok(new ApiResponse<>(true, "Seats generated successfully. Total: " + seats.size(), seats));
    }
}
