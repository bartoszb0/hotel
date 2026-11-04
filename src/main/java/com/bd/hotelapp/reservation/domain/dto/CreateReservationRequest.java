package com.bd.hotelapp.reservation.domain.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record CreateReservationRequest(
        @NotNull(message = "Room ID is required")
        UUID roomId,

        @NotBlank(message = "Guest first name is required")
        @Size(max = 100, message = "Guest first name can not be longer than 100 characters")
        String guestFirstName,

        @NotBlank(message = "Guest last name is required")
        @Size(max = 100, message = "Guest last name can not be longer than 100 characters")
        String guestLastName,

        @NotBlank(message = "Guest email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Guest email can not be longer than 255 characters")
        String guestEmail,

        @NotNull(message = "Check in date is required")
        LocalDate checkIn,

        @NotNull(message = "Check out date is required")
        LocalDate checkOut,

        @NotNull(message = "Guest count is required")
        @Positive(message = "Guest count must be bigger than 0")
        Integer guestCount
) {
}
