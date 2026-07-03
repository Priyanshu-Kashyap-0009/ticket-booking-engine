package com.priyanshu.booking.controller;

import com.priyanshu.booking.model.Event;
import com.priyanshu.booking.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(value = "events", key = "#id")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "events", key = "#id")
    public Event updateEvent(@PathVariable Long id,
                             @RequestBody Event updatedEvent) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setName(updatedEvent.getName());
        event.setDate(updatedEvent.getDate());
        event.setTotalSeats(updatedEvent.getTotalSeats());
        event.setAvailableSeats(updatedEvent.getAvailableSeats());
        return eventRepository.save(event);
    }
}