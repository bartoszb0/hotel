package com.bd.hotelapp.user.exception;

import com.bd.hotelapp.common.exception.NotFoundException;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String email) {
        super("User with email '%s' not found".formatted(email));
    }

    public UserNotFoundException(UUID id) {
        super("User with id '%s' not found".formatted(id));
    }
}
