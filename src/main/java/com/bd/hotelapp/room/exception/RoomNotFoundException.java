package com.bd.hotelapp.room.exception;

import com.bd.hotelapp.common.exception.NotFoundException;

import java.util.UUID;

public class RoomNotFoundException extends NotFoundException {
    public RoomNotFoundException(UUID id) {
        super("Room with id %s not found".formatted(id));
    }
}
