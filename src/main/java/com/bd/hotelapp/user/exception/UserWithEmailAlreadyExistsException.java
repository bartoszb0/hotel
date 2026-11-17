package com.bd.hotelapp.user.exception;

import com.bd.hotelapp.common.exception.ConflictException;

public class UserWithEmailAlreadyExistsException extends ConflictException {
    public UserWithEmailAlreadyExistsException(String email) {
        super("User with email '%s' already exists".formatted(email));
    }
}
