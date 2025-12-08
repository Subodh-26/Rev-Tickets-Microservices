package com.revature.movieservice.service;

import com.revature.movieservice.entity.Event;
import com.revature.movieservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllActiveEvents() {
        return eventRepository.findByIsActiveTrue();
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEventDateGreaterThanEqualAndIsActiveTrue(LocalDate.now());
    }

    public Event activateEvent(Long id) {
        Event event = getEventById(id);
        event.setIsActive(true);
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        event.setIsActive(false);
        eventRepository.save(event);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        Event event = getEventById(id);
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setCategory(eventDetails.getCategory());
        event.setArtistOrTeam(eventDetails.getArtistOrTeam());
        event.setAgeRestriction(eventDetails.getAgeRestriction());
        event.setDurationMinutes(eventDetails.getDurationMinutes());
        event.setEventDate(eventDetails.getEventDate());
        event.setEventTime(eventDetails.getEventTime());
        event.setLanguage(eventDetails.getLanguage());
        return eventRepository.save(event);
    }
}
