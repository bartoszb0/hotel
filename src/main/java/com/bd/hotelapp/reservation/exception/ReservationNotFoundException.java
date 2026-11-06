package com.bd.hotelapp.reservation.exception;

import com.bd.hotelapp.common.exception.NotFoundException;

import java.util.UUID;

public class ReservationNotFoundException extends NotFoundException {
    public ReservationNotFoundException(UUID id) {
        super("Reservation with id %s not found".formatted(id));
    }
}
