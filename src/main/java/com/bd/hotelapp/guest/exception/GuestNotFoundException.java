package com.bd.hotelapp.guest.exception;

import com.bd.hotelapp.common.exception.NotFoundException;

import java.util.UUID;

public class GuestNotFoundException extends NotFoundException {
    public GuestNotFoundException(UUID id) {
        super("Guest with id '%s' not found".formatted(id));
    }
}
