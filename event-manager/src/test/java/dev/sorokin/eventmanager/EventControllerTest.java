package dev.sorokin.eventmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventmanager.dto.EventCreateRequestDTO;
import dev.sorokin.eventmanager.dto.EventResponseDTO;
import dev.sorokin.eventmanager.event.Event;
import dev.sorokin.eventmanager.event.EventController;
import dev.sorokin.eventmanager.event.EventService;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.rateLimiter.RateLimiterFilter;
import dev.sorokin.eventmanager.registration.RegistrationService;
import dev.sorokin.eventmanager.security.JwtTokenFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты только веб-слоя: EventService/EventMapper/RegistrationService замоканы,
 * security-фильтры отключены (addFilters = false) и отдельно замоканы Component-фильтры, проверяем исключительно
 * маршрутизацию, (де)сериализацию JSON, коды статусов и валидацию @Valid.
 */
@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private EventMapper eventMapper;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private RateLimiterFilter rateLimiterFilter;

    @MockitoBean
    private JwtTokenFilter jwtTokenFilter;

    @Test
    void createEvent_withValidBody_returnsCreatedWithBody() throws Exception {

        EventCreateRequestDTO request = new EventCreateRequestDTO(
                LocalDateTime.now().plusDays(1), 60, 100, 10, 1L, "Conference");

        Event mappedFromDto = new Event("Conference", request.getStartAt(), 60, 10, 1L, 0, "WAIT_START", 100);
        Event createdEvent = new Event("Conference", request.getStartAt(), 60, 10, 1L, 0, "WAIT_START", 100);
        createdEvent.setId(42L);

        EventResponseDTO responseDto = new EventResponseDTO(
                42L, "Conference", request.getStartAt(), 60, 100, 10, 0, 1L, 7L, "WAIT_START");

        when(eventMapper.toEventFromDto(any(EventCreateRequestDTO.class))).thenReturn(mappedFromDto);
        when(eventService.createEvent(mappedFromDto)).thenReturn(createdEvent);
        when(eventMapper.toResponseDtoFromEvent(createdEvent)).thenReturn(responseDto);

        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Conference"))
                .andExpect(jsonPath("$.status").value("WAIT_START"));
    }

    @Test
    void getEventById_returnsOkWithBody() throws Exception {

        Event event = new Event("Meetup", LocalDateTime.now().plusDays(2), 45, 15, 2L, 3, "WAIT_START", 0);
        event.setId(5L);
        EventResponseDTO responseDto = new EventResponseDTO(
                5L, "Meetup", event.getStartAt(), 45, 0, 15, 3, 2L, 9L, "WAIT_START");

        when(eventService.getEventById(5L)).thenReturn(event);
        when(eventMapper.toResponseDtoFromEvent(event)).thenReturn(responseDto);

        mockMvc.perform(get("/events/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Meetup"));
    }

    @Test
    void createEvent_withInvalidBody_returnsBadRequestAndSkipsService() throws Exception {

        String invalidJson = """
                {
                  "startAt": "2020-01-01T10:00:00",
                  "durationMinutes": 60,
                  "cost": 100,
                  "maxPlaces": 1,
                  "locationId": null,
                  "name": ""
                }
                """;

        mockMvc.perform(post("/events")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации"));

        verify(eventService, never()).createEvent(any());
    }
}

