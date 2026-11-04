package com.bd.hotelapp.reservation.domain.dto;

import com.bd.hotelapp.guest.domain.dto.GuestResponse;
import com.bd.hotelapp.reservation.domain.entity.ReservationStatus;
import com.bd.hotelapp.room.domain.dto.RoomResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        RoomResponse room,
        GuestResponse guest,
        LocalDate checkIn,
        LocalDate checkOut,
        Integer guestCount,
        ReservationStatus status,
        BigDecimal totalPrice,
        Instant createdAt
) {
}
