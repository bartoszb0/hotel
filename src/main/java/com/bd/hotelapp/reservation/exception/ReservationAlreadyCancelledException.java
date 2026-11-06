package com.bd.hotelapp.reservation.exception;

import com.bd.hotelapp.common.exception.BusinessRuleException;

import java.util.UUID;

public class ReservationAlreadyCancelledException extends BusinessRuleException {
    public ReservationAlreadyCancelledException(UUID id) {
        super("Reservation %s is already cancelled".formatted(id));
    }
}
