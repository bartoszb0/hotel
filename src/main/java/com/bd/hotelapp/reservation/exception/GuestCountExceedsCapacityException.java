package com.bd.hotelapp.reservation.exception;

import com.bd.hotelapp.common.exception.BusinessRuleException;

public class GuestCountExceedsCapacityException extends BusinessRuleException {
    public GuestCountExceedsCapacityException(int guestCount, int capacity) {
        super("Guest count %d exceeds room capacity of %d".formatted(guestCount, capacity));
    }
}