package com.bd.hotelapp.roomtype.domain.command;

import java.math.BigDecimal;

public record CreateRoomTypeCommand(
        String name,
        Integer capacity,
        BigDecimal pricePerNight
) {
}
