package com.bd.hotelapp.reservation.exception;

import com.bd.hotelapp.common.exception.ConflictException;

public class RoomNotAvailableException extends ConflictException {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}
