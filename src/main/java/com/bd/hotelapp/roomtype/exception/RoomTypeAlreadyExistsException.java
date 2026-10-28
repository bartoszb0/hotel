package com.bd.hotelapp.roomtype.exception;

import com.bd.hotelapp.common.exception.ConflictException;

public class RoomTypeAlreadyExistsException extends ConflictException {
    public RoomTypeAlreadyExistsException(String name) {
        super("Room type with name '%s' already exists".formatted(name));
    }
}
