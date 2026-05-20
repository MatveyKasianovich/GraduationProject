package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record LocationDTO(
        @Null
        Long id,

        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotNull
        @Min(5)
        int capacity,

        String description){}

