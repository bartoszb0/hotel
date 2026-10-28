package com.bd.hotelapp.room.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRoomRequest(
        @NotNull(message = "Room number must be provided")
        @Min(value = 1, message = "Room number must be at least 1")
        Integer number,

        @NotNull(message = "Room type ID must be provided")
        UUID roomTypeId
) {
}
