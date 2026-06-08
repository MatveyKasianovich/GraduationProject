package dev.sorokin.eventmanager.event;


import dev.sorokin.eventmanager.dto.EventCreateRequestDTO;
import dev.sorokin.eventmanager.dto.EventResponseDTO;
import dev.sorokin.eventmanager.dto.EventSearchRequestDTO;
import dev.sorokin.eventmanager.dto.EventUpdateRequestDTO;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.registration.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final RegistrationService registrationService;
    private final EventMapper eventMapper;
    private final EventService eventService;

    public EventController(RegistrationService registrationService, EventMapper eventMapper, EventService eventService) {
        this.registrationService = registrationService;
        this.eventMapper = eventMapper;
        this.eventService = eventService;
    }


    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody @Valid EventCreateRequestDTO eventCreateRequestDTO){
        Event eventToCreate = eventMapper.toEventFromDto(eventCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toResponseDtoFromEvent(eventService.createEvent(eventToCreate)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO>getEventById(@PathVariable Long id){
        Event event = eventService.getEventById(id);
        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.toResponseDtoFromEvent(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO>updateEventById(@RequestBody @Valid EventUpdateRequestDTO eventUpdateRequestDTO, @PathVariable Long id){
        Event eventToUpdate = eventMapper.toEventFromUpdateDTO(eventUpdateRequestDTO);
        Event updatedEvent = eventService.updateEventById(eventToUpdate,id);
        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.toResponseDtoFromEvent(updatedEvent));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsByOwner(){
        List<EventResponseDTO>eventsDTO = eventService.getAllEventsByOwner().stream()
                .map(event -> eventMapper.toResponseDtoFromEvent(event))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(eventsDTO);
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventResponseDTO>>getAllEventsByFilter(@RequestBody EventSearchRequestDTO eventSearchRequestDTO){
        List<Event> events = eventService.getAllEventsByFilter(eventMapper.toSearchFromDto(eventSearchRequestDTO));
        List<EventResponseDTO>eventsDTO = events.stream()
                .map(event -> eventMapper.toResponseDtoFromEvent(event))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(eventsDTO);
    }

    @PostMapping("/registrations/{eventId}")
    public ResponseEntity<Void>eventRegistration(@PathVariable Long eventId){
        registrationService.createRegistration(eventId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/registrations/cancel/{eventId}")
    public ResponseEntity<Void>cancelregistration(@PathVariable Long eventId){
        registrationService.cancelRegistration(eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/registrations/my")
    public ResponseEntity<List<EventResponseDTO>> getMyRegistrations(){
        return ResponseEntity.status(HttpStatus.OK).body(registrationService.getMyRegistrations().stream()
                .map(event -> eventMapper.toResponseDtoFromEvent(event))
                .toList());
    }


}
