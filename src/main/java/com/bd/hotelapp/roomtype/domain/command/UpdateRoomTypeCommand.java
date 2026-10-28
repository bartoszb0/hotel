package com.bd.hotelapp.roomtype.domain.command;

import java.math.BigDecimal;

public record UpdateRoomTypeCommand(
        String name,
        Integer capacity,
        BigDecimal pricePerNight
) {
}
