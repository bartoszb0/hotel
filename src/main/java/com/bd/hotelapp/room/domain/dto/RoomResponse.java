package com.bd.hotelapp.room.domain.dto;

import com.bd.hotelapp.roomtype.domain.dto.RoomTypeResponse;

import java.util.UUID;

public record RoomResponse(
        UUID id,
        Integer number,
        RoomTypeResponse roomType
) {
}
