package com.bd.hotelapp.roomtype.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RoomTypeResponse(
        UUID id,
        String name,
        Integer capacity,
        BigDecimal pricePerNight
) {
}
