package com.revature.revtickets.controller;

import com.revature.revtickets.entity.*;
import com.revature.revtickets.repository.*;
import com.revature.revtickets.response.ApiResponse;
import com.revature.revtickets.service.OpenEventShowService;
import com.revature.revtickets.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/shows")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminShowController {

    @Autowired
    private ShowRepository showRepository;
    
    @Autowired
    private OpenEventShowService openEventShowService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatService seatService;
    
    @Autowired
    private com.revature.revtickets.repository.SeatRepository seatRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllShows() {
        try {
            List<Show> regularShows = showRepository.findAll();
            List<OpenEventShow> openEventShows = openEventShowService.getAllShows();
            
            Map<String, Object> response = new HashMap<>();
            response.put("regularShows", regularShows);
            response.put("openEventShows", openEventShows);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Shows fetched successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch shows: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Show>> getShowById(@PathVariable Long id) {
        try {
            Show show = showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Show fetched successfully", show));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to fetch show: " + e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createShow(@RequestBody Map<String, Object> showData) {
        try {
            System.out.println("📝 Received show data: " + showData);
            
            String showType = (String) showData.get("showType");
            Boolean isOpenGround = (Boolean) showData.get("isOpenGround");
            
            System.out.println("🎬 Show type: " + showType);
            System.out.println("🌟 Is Open Ground: " + isOpenGround);
            
            // Check if this is an open ground event show
            if (isOpenGround != null && isOpenGround) {
                // Create OpenEventShow
                OpenEventShow openShow = new OpenEventShow();
                
                // Must be an EVENT type
                if (!"EVENT".equals(showType)) {
                    throw new RuntimeException("Open ground shows are only supported for events");
                }
                
                Object eventIdObj = showData.get("eventId");
                if (eventIdObj != null && !"".equals(eventIdObj.toString()) && !"null".equals(eventIdObj.toString())) {
                    Long eventId = Long.parseLong(eventIdObj.toString());
                    System.out.println("🎪 Event ID: " + eventId);
                    Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
                    openShow.setEvent(event);
                } else {
                    throw new RuntimeException("Event ID is required for open ground shows");
                }
                
                openShow.setShowDate(LocalDate.parse((String) showData.get("showDate")));
                openShow.setShowTime(LocalTime.parse((String) showData.get("showTime")));
                
                // Handle pricing zones
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> pricingZones = (List<Map<String, Object>>) showData.get("pricingZones");
                if (pricingZones == null || pricingZones.isEmpty()) {
                    throw new RuntimeException("Pricing zones are required for open ground shows");
                }
                
                openShow.setPricingZones(pricingZones);
                
                // Calculate total capacity from pricing zones
                int totalCapacity = 0;
                for (Map<String, Object> zone : pricingZones) {
                    Object capacityObj = zone.get("capacity");
                    if (capacityObj != null) {
                        totalCapacity += Integer.parseInt(capacityObj.toString());
                    }
                }
                
                openShow.setTotalCapacity(totalCapacity);
                openShow.setAvailableCapacity(totalCapacity);
                openShow.setIsActive(true);
                
                System.out.println("💾 Saving open event show...");
                OpenEventShow savedShow = openEventShowService.createShow(openShow);
                System.out.println("✅ Open event show saved successfully with ID: " + savedShow.getOpenShowId());
                return ResponseEntity.ok(new ApiResponse<>(true, "Open event show created successfully", savedShow));
                
            } else {
                // Create regular Show
                Show show = new Show();
                
                if ("MOVIE".equals(showType)) {
                    Object movieIdObj = showData.get("movieId");
                    if (movieIdObj != null && !"".equals(movieIdObj.toString()) && !"null".equals(movieIdObj.toString())) {
                        Long movieId = Long.parseLong(movieIdObj.toString());
                        System.out.println("🎥 Movie ID: " + movieId);
                        Movie movie = movieRepository.findById(movieId)
                            .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));
                        show.setMovie(movie);
                    } else {
                        throw new RuntimeException("Movie ID is required for MOVIE show type");
                    }
                } else if ("EVENT".equals(showType)) {
                    Object eventIdObj = showData.get("eventId");
                    if (eventIdObj != null && !"".equals(eventIdObj.toString()) && !"null".equals(eventIdObj.toString())) {
                        Long eventId = Long.parseLong(eventIdObj.toString());
                        System.out.println("🎪 Event ID: " + eventId);
                        Event event = eventRepository.findById(eventId)
                            .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
                        show.setEvent(event);
                    } else {
                        throw new RuntimeException("Event ID is required for EVENT show type");
                    }
                } else {
                    throw new RuntimeException("Show type must be either MOVIE or EVENT");
                }

                // Regular venue setup
                Long venueId = Long.parseLong(showData.get("venueId").toString());
                System.out.println("🏛️ Venue ID: " + venueId);
                Venue venue = venueRepository.findById(venueId)
                    .orElseThrow(() -> new RuntimeException("Venue not found with ID: " + venueId));
                show.setVenue(venue);

                Long screenId = Long.parseLong(showData.get("screenId").toString());
                System.out.println("📺 Screen ID: " + screenId);
                Screen screen = screenRepository.findById(screenId)
                    .orElseThrow(() -> new RuntimeException("Screen not found with ID: " + screenId));
                show.setScreen(screen);
                
                Integer totalSeats = screen.getTotalSeats();
                show.setTotalSeats(totalSeats);
                show.setAvailableSeats(totalSeats);
                
                show.setShowDate(LocalDate.parse((String) showData.get("showDate")));
                show.setShowTime(LocalTime.parse((String) showData.get("showTime")));
                
                // Regular show pricing
                BigDecimal standardPrice = new BigDecimal(showData.get("standardPrice").toString());
                show.setBasePrice(standardPrice);

                Map<String, BigDecimal> pricingTiers = new HashMap<>();
                pricingTiers.put("standard", new BigDecimal(showData.get("standardPrice").toString()));
                pricingTiers.put("premium", new BigDecimal(showData.get("premiumPrice").toString()));
                pricingTiers.put("vip", new BigDecimal(showData.get("vipPrice").toString()));
                show.setPricingTiers(pricingTiers);

                show.setIsActive(true);

                System.out.println("💾 Saving show...");
                Show savedShow = showRepository.save(show);
                System.out.println("✅ Show saved successfully with ID: " + savedShow.getShowId());
                // Try to auto-generate seats for this show (use screen layout if available)
                try {
                    int totalRows = 10;
                    int seatsPerRow = 26;
                    Map<String, Object> seatLayoutMap = savedShow.getScreen().getSeatLayout();
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
                    List<com.revature.revtickets.entity.Seat> generated = seatService.generateSeatsForShow(savedShow.getShowId(), totalRows, seatsPerRow);
                    System.out.println("ℹ️ Generated " + generated.size() + " seats for show " + savedShow.getShowId());
                } catch (Exception e) {
                    System.err.println("Failed to generate seats for saved show: " + e.getMessage());
                    e.printStackTrace();
                }
                return ResponseEntity.ok(new ApiResponse<>(true, "Show created successfully", savedShow));
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating show: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to create show: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Show>> updateShow(@PathVariable Long id, @RequestBody Map<String, Object> showData) {
        try {
            Show show = showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found"));
            
            String showType = (String) showData.get("showType");
            if ("MOVIE".equals(showType) && showData.get("movieId") != null) {
                Long movieId = Long.parseLong(showData.get("movieId").toString());
                Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
                show.setMovie(movie);
                show.setEvent(null);
            } else if ("EVENT".equals(showType) && showData.get("eventId") != null) {
                Long eventId = Long.parseLong(showData.get("eventId").toString());
                Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
                show.setEvent(event);
                show.setMovie(null);
            }

            Long venueId = Long.parseLong(showData.get("venueId").toString());
            Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));
            show.setVenue(venue);

            Long screenId = Long.parseLong(showData.get("screenId").toString());
            Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen not found"));
            show.setScreen(screen);
            
            Integer totalSeats = screen.getTotalSeats();
            show.setTotalSeats(totalSeats);
            show.setAvailableSeats(totalSeats);

            show.setShowDate(LocalDate.parse((String) showData.get("showDate")));
            show.setShowTime(LocalTime.parse((String) showData.get("showTime")));
            
            BigDecimal standardPrice = new BigDecimal(showData.get("standardPrice").toString());
            show.setBasePrice(standardPrice);

            Map<String, BigDecimal> pricingTiers = new HashMap<>();
            pricingTiers.put("standard", new BigDecimal(showData.get("standardPrice").toString()));
            pricingTiers.put("premium", new BigDecimal(showData.get("premiumPrice").toString()));
            pricingTiers.put("vip", new BigDecimal(showData.get("vipPrice").toString()));
            show.setPricingTiers(pricingTiers);

            Show savedShow = showRepository.save(show);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show updated successfully", savedShow));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to update show: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShow(@PathVariable Long id) {
        try {
            Show show = showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found"));
            showRepository.delete(show);
            return ResponseEntity.ok(new ApiResponse<>(true, "Show deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Failed to delete show: " + e.getMessage(), null));
        }
    }

    @PostMapping("/generate-all-seats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateAllMissingSeats() {
        Map<String, Object> result = new HashMap<>();
        int totalGenerated = 0;
        List<Long> processedShows = new ArrayList<>();

        List<Show> allShows = showRepository.findAll();
        for (Show s : allShows) {
            Long sid = s.getShowId();
            List<com.revature.revtickets.entity.Seat> existing = seatRepository.findByShowShowId(sid);
            if (existing == null || existing.isEmpty()) {
                try {
                    int totalRows = 10;
                    int seatsPerRow = 26;
                    Map<String, Object> seatLayoutMap = s.getScreen() != null ? s.getScreen().getSeatLayout() : null;
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
                    List<com.revature.revtickets.entity.Seat> gen = seatService.generateSeatsForShow(sid, totalRows, seatsPerRow);
                    totalGenerated += gen.size();
                    processedShows.add(sid);
                } catch (Exception ex) {
                    System.err.println("Failed to generate seats for show " + sid + ": " + ex.getMessage());
                }
            }
        }

        result.put("generatedCount", totalGenerated);
        result.put("processedShows", processedShows);
        return ResponseEntity.ok(new ApiResponse<>(true, "Generated seats for missing shows", result));
    }
}
