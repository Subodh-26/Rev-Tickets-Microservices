package com.revature.bookingservice.service;

import com.revature.bookingservice.entity.Show;
import com.revature.bookingservice.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;
    
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("simpleRestTemplate")
    private RestTemplate restTemplate;
    
    @Autowired
    private SeatService seatService;

    public List<Show> getAllActiveShows() {
        return showRepository.findByIsActiveTrueOrderByShowDateAscShowTimeAsc();
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public List<java.util.Map<String, Object>> getAllShowsWithDetails() {
        List<Show> shows = showRepository.findAll();
        
        // Fetch all movies, events, venues, screens at once
        java.util.Map<Long, Object> movieCache = new java.util.HashMap<>();
        java.util.Map<Long, Object> eventCache = new java.util.HashMap<>();
        java.util.Map<Long, Object> venueCache = new java.util.HashMap<>();
        java.util.Map<Long, Object> screenCache = new java.util.HashMap<>();
        
        // Collect unique IDs
        java.util.Set<Long> movieIds = shows.stream().map(Show::getMovieId).filter(id -> id != null).collect(java.util.stream.Collectors.toSet());
        java.util.Set<Long> eventIds = shows.stream().map(Show::getEventId).filter(id -> id != null).collect(java.util.stream.Collectors.toSet());
        java.util.Set<Long> venueIds = shows.stream().map(Show::getVenueId).collect(java.util.stream.Collectors.toSet());
        java.util.Set<Long> screenIds = shows.stream().map(Show::getScreenId).collect(java.util.stream.Collectors.toSet());
        
        // Fetch all at once
        for (Long id : movieIds) {
            try {
                java.util.Map response = restTemplate.getForObject("http://localhost:8082/api/movies/" + id, java.util.Map.class);
                if (response != null && response.get("data") != null) movieCache.put(id, response.get("data"));
            } catch (Exception e) {}
        }
        
        for (Long id : eventIds) {
            try {
                java.util.Map response = restTemplate.getForObject("http://localhost:8082/api/events/" + id, java.util.Map.class);
                if (response != null && response.get("data") != null) eventCache.put(id, response.get("data"));
            } catch (Exception e) {}
        }
        
        for (Long id : venueIds) {
            try {
                java.util.Map response = restTemplate.getForObject("http://localhost:8083/api/venues/" + id, java.util.Map.class);
                if (response != null && response.get("data") != null) venueCache.put(id, response.get("data"));
            } catch (Exception e) {}
        }
        
        for (Long id : screenIds) {
            try {
                java.util.Map response = restTemplate.getForObject("http://localhost:8083/api/venues/screens/" + id, java.util.Map.class);
                if (response != null && response.get("data") != null) screenCache.put(id, response.get("data"));
            } catch (Exception e) {}
        }
        
        // Now enrich shows using cache
        return shows.stream().map(show -> {
            java.util.Map<String, Object> enriched = new java.util.HashMap<>();
            enriched.put("showId", show.getShowId());
            enriched.put("movieId", show.getMovieId());
            enriched.put("eventId", show.getEventId());
            enriched.put("venueId", show.getVenueId());
            enriched.put("screenId", show.getScreenId());
            enriched.put("showDate", show.getShowDate());
            enriched.put("showTime", show.getShowTime());
            enriched.put("basePrice", show.getBasePrice());
            enriched.put("totalSeats", show.getTotalSeats());
            enriched.put("availableSeats", show.getAvailableSeats());
            enriched.put("isActive", show.getIsActive());
            
            if (show.getMovieId() != null) enriched.put("movie", movieCache.get(show.getMovieId()));
            if (show.getEventId() != null) enriched.put("event", eventCache.get(show.getEventId()));
            enriched.put("venue", venueCache.get(show.getVenueId()));
            enriched.put("screen", screenCache.get(show.getScreenId()));
            
            return enriched;
        }).collect(java.util.stream.Collectors.toList());
    }

    private java.util.Map<String, Object> enrichShowWithDetails(Show show) {
        java.util.Map<String, Object> enriched = new java.util.HashMap<>();
        enriched.put("showId", show.getShowId());
        enriched.put("movieId", show.getMovieId());
        enriched.put("eventId", show.getEventId());
        enriched.put("venueId", show.getVenueId());
        enriched.put("screenId", show.getScreenId());
        enriched.put("showDate", show.getShowDate());
        enriched.put("showTime", show.getShowTime());
        enriched.put("basePrice", show.getBasePrice());
        enriched.put("totalSeats", show.getTotalSeats());
        enriched.put("availableSeats", show.getAvailableSeats());
        enriched.put("isActive", show.getIsActive());

        if (show.getMovieId() != null) {
            try {
                String url = "http://movie-service/api/movies/" + show.getMovieId();
                java.util.Map response = restTemplate.getForObject(url, java.util.Map.class);
                if (response != null && response.get("data") != null) {
                    enriched.put("movie", response.get("data"));
                }
            } catch (Exception e) {}
        }

        if (show.getEventId() != null) {
            try {
                String url = "http://movie-service/api/events/" + show.getEventId();
                java.util.Map response = restTemplate.getForObject(url, java.util.Map.class);
                if (response != null && response.get("data") != null) {
                    enriched.put("event", response.get("data"));
                }
            } catch (Exception e) {}
        }

        try {
            String url = "http://venue-service/api/venues/" + show.getVenueId();
            java.util.Map response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null && response.get("data") != null) {
                enriched.put("venue", response.get("data"));
            }
        } catch (Exception e) {}

        try {
            String url = "http://venue-service/api/venues/screens/" + show.getScreenId();
            java.util.Map response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null && response.get("data") != null) {
                enriched.put("screen", response.get("data"));
            }
        } catch (Exception e) {}

        return enriched;
    }

    public Show getShowById(Long id) {
        return showRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Show not found"));
    }

    public Show createShow(Show show) {
        return showRepository.save(show);
    }

    public Show updateShow(Long id, Show showDetails) {
        Show show = getShowById(id);
        show.setMovieId(showDetails.getMovieId());
        show.setEventId(showDetails.getEventId());
        show.setVenueId(showDetails.getVenueId());
        show.setScreenId(showDetails.getScreenId());
        show.setShowDate(showDetails.getShowDate());
        show.setShowTime(showDetails.getShowTime());
        show.setBasePrice(showDetails.getBasePrice());
        show.setPricingTiers(showDetails.getPricingTiers());
        show.setTotalSeats(showDetails.getTotalSeats());
        show.setAvailableSeats(showDetails.getAvailableSeats());
        return showRepository.save(show);
    }

    public void deleteShow(Long id) {
        Show show = getShowById(id);
        show.setIsActive(false);
        showRepository.save(show);
    }

    public List<Long> getAllShowIds() {
        return showRepository.findAll().stream()
            .map(Show::getShowId)
            .collect(java.util.stream.Collectors.toList());
    }

    public List<String> getShowDatesByMovieId(Long movieId) {
        return showRepository.findAll().stream()
            .filter(show -> movieId.equals(show.getMovieId()) && Boolean.TRUE.equals(show.getIsActive()))
            .map(show -> show.getShowDate().toString())
            .distinct()
            .sorted()
            .collect(java.util.stream.Collectors.toList());
    }

    public List<String> getShowDatesByEventId(Long eventId) {
        return showRepository.findAll().stream()
            .filter(show -> eventId.equals(show.getEventId()) && Boolean.TRUE.equals(show.getIsActive()))
            .map(show -> show.getShowDate().toString())
            .distinct()
            .sorted()
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Show> getShowsByMovieIdAndDate(Long movieId, String date) {
        java.time.LocalDate showDate = java.time.LocalDate.parse(date);
        return showRepository.findAll().stream()
            .filter(show -> movieId.equals(show.getMovieId()) 
                && showDate.equals(show.getShowDate()) 
                && Boolean.TRUE.equals(show.getIsActive()))
            .sorted((a, b) -> a.getShowTime().compareTo(b.getShowTime()))
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Show> getShowsByEventIdAndDate(Long eventId, String date) {
        java.time.LocalDate showDate = java.time.LocalDate.parse(date);
        return showRepository.findAll().stream()
            .filter(show -> eventId.equals(show.getEventId()) 
                && showDate.equals(show.getShowDate()) 
                && Boolean.TRUE.equals(show.getIsActive()))
            .sorted((a, b) -> a.getShowTime().compareTo(b.getShowTime()))
            .collect(java.util.stream.Collectors.toList());
    }

    public Show activateShow(Long id) {
        Show show = getShowById(id);
        show.setIsActive(true);
        return showRepository.save(show);
    }

    public void generateSeatsForShow(Long showId) {
        Show show = getShowById(showId);
        seatService.generateSeatsForShow(showId, show.getTotalSeats(), show.getBasePrice());
    }
    
    public Show createShowFromData(Map<String, Object> showData) {
        Show show = new Show();
        
        if (showData.get("movieId") != null) {
            show.setMovieId(Long.parseLong(showData.get("movieId").toString()));
        }
        if (showData.get("eventId") != null) {
            show.setEventId(Long.parseLong(showData.get("eventId").toString()));
        }
        if (showData.get("venueId") != null) {
            show.setVenueId(Long.parseLong(showData.get("venueId").toString()));
        }
        
        Long screenId = null;
        if (showData.get("screenId") != null) {
            screenId = Long.parseLong(showData.get("screenId").toString());
            show.setScreenId(screenId);
        }
        
        if (showData.get("showDate") != null) {
            show.setShowDate(java.time.LocalDate.parse(showData.get("showDate").toString()));
        }
        if (showData.get("showTime") != null) {
            show.setShowTime(java.time.LocalTime.parse(showData.get("showTime").toString()));
        }
        if (showData.get("basePrice") != null) {
            show.setBasePrice(new java.math.BigDecimal(showData.get("basePrice").toString()));
        } else if (showData.get("standardPrice") != null) {
            show.setBasePrice(new java.math.BigDecimal(showData.get("standardPrice").toString()));
        } else {
            show.setBasePrice(new java.math.BigDecimal("200"));
        }
        
        // Get screen capacity from venue service
        int totalSeats = 100; // default
        if (screenId != null) {
            try {
                String url = "http://venue-service/api/venues/screens/" + screenId;
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                if (response != null && response.get("data") != null) {
                    Map<String, Object> screenData = (Map<String, Object>) response.get("data");
                    if (screenData.get("capacity") != null) {
                        totalSeats = Integer.parseInt(screenData.get("capacity").toString());
                    }
                }
            } catch (Exception e) {
                // Use default if service call fails
            }
        }
        
        show.setTotalSeats(totalSeats);
        show.setAvailableSeats(totalSeats);
        show.setIsActive(true);
        
        Show savedShow = showRepository.save(show);
        
        // Generate seats
        seatService.generateSeatsForShow(savedShow.getShowId(), totalSeats, show.getBasePrice());
        
        return savedShow;
    }
}