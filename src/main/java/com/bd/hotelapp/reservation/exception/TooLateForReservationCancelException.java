package com.bd.hotelapp.reservation.exception;

import com.bd.hotelapp.common.exception.BusinessRuleException;

import java.util.UUID;

public class TooLateForReservationCancelException extends BusinessRuleException {
    public TooLateForReservationCancelException(UUID id) {
        super("Reservation %s cannot be cancelled on or after the check-in date".formatted(id));
    }
}
