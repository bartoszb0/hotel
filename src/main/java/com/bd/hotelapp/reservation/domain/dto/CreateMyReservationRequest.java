package com.bd.hotelapp.reservation.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMyReservationRequest(
        @NotNull(message = "Room ID is required")
        UUID roomId,

        @NotNull(message = "Check in date is required")
        LocalDate checkIn,

        @NotNull(message = "Check out date is required")
        LocalDate checkOut,

        @NotNull(message = "Guest count is required")
        @Positive(message = "Guest count must be bigger than 0")
        Integer guestCount
) {
}
