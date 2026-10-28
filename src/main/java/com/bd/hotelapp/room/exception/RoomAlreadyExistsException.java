package com.bd.hotelapp.room.exception;

import com.bd.hotelapp.common.exception.ConflictException;

public class RoomAlreadyExistsException extends ConflictException {
    public RoomAlreadyExistsException(Integer number) {
        super("Room with number '%s' already exists".formatted(number));
    }
}
