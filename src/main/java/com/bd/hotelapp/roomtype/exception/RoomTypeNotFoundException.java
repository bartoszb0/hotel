package com.bd.hotelapp.roomtype.exception;

import com.bd.hotelapp.common.exception.NotFoundException;

import java.util.UUID;

public class RoomTypeNotFoundException extends NotFoundException {
    public RoomTypeNotFoundException(UUID id) {
        super("Room type with id %s not found".formatted(id));
    }
}
