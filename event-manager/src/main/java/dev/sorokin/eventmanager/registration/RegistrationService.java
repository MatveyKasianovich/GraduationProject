package dev.sorokin.eventmanager.registration;

import dev.sorokin.eventmanager.event.Event;
import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.event.EventStatus;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.security.SecurityUtils;
import dev.sorokin.eventmanager.user.User;
import dev.sorokin.eventmanager.user.UserEntity;
import dev.sorokin.eventmanager.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public RegistrationService(UserRepository userRepository, RegistrationRepository registrationRepository, EventRepository eventRepository, EventMapper eventMapper) {
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public void createRegistration(Long eventId) {

        EventEntity eventEntity=eventRepository.findById(eventId)
                .orElseThrow(()->new EntityNotFoundException("No event with id=%s".formatted(eventId)));

        if (eventEntity.getOccupiedPlaces() == eventEntity.getMaxPlaces()){
            throw new IllegalArgumentException("This event is overcrowded");
        }
        else if(!eventEntity.getStatus().equals(EventStatus.WAIT_START.name())){
            throw new IllegalArgumentException("Event has already started or cancelled");
        }

        Long currentUserId=SecurityUtils.getCurrentUserId();
        Optional<RegistrationEntity> registration = registrationRepository.findCurrentUserRegistrationOnEvent(currentUserId,eventId);
        if(registration.isPresent()){
            throw new IllegalArgumentException("User was already registered for event with id=%s".formatted(eventId));
        }else {
            eventEntity.setOccupiedPlaces(eventEntity.getOccupiedPlaces()+1);
            registrationRepository.save(new RegistrationEntity(
                    eventEntity,
                    userRepository.getReferenceById(SecurityUtils.getCurrentUser().getId()),
                    LocalDateTime.now()
            ));
        }
    }

    @Transactional
    public void cancelRegistration(Long eventId) {

        EventEntity eventEntity=eventRepository.findById(eventId)
                .orElseThrow(()->new EntityNotFoundException("No event with id=%s".formatted(eventId)));

        if(!eventEntity.getStatus().equals(EventStatus.WAIT_START.name())){
            throw new IllegalArgumentException("Event has already started or cancelled");
        }

        Long currentUserId=SecurityUtils.getCurrentUserId();
        Optional<RegistrationEntity> registration = registrationRepository.findCurrentUserRegistrationOnEvent(currentUserId,eventId);
        if(registration.isPresent()){
            eventEntity.setOccupiedPlaces(eventEntity.getOccupiedPlaces()-1);
            registrationRepository.delete(registration.get());
        }else {
            throw new IllegalArgumentException("User was not registered for event with id=%s".formatted(eventId));
        }

    }

    public List<Event> getMyRegistrations() {

        Long currentUserId = SecurityUtils.getCurrentUser().getId();

        List<EventEntity> eventEntities = eventRepository.findEventsByUserRegistration(currentUserId);

        if(eventEntities.isEmpty()){
            throw new EntityNotFoundException("User was not registered on any events");
        }
        else {
            return eventEntities.stream()
                    .map(eventEntity -> eventMapper.toEventFromEntity(eventEntity))
                    .toList();
        }
    }
}
