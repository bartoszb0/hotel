package com.bd.hotelapp.reservation.domain.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateReservationCommand(
        UUID roomId,
        String guestFirstName,
        String guestLastName,
        String guestEmail,
        LocalDate checkIn,
        LocalDate checkOut,
        Integer guestCount
) {
}
