package dev.sorokin.eventmanager.registration;

import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class Registration {

    private Long id;
    private EventEntity event;
    private UserEntity user;
    private LocalDateTime createdAt;

    public Registration(Long id, EventEntity event, UserEntity user, LocalDateTime createdAt) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Registration(EventEntity event, UserEntity user, LocalDateTime createdAt) {
        this.event = event;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
