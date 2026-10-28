package com.bd.hotelapp.room.domain.dto;

import jakarta.validation.constraints.Min;

import java.util.UUID;

public record UpdateRoomRequest(

        @Min(value = 1, message = "Room number must be at least 1")
        Integer number,

        UUID roomTypeId
) {
}
