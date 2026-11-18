package com.bd.hotelapp.reservation.domain.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMyReservationCommand(
        UUID userId,
        UUID roomId,
        LocalDate checkIn,
        LocalDate checkOut,
        Integer guestCount
) {
}
