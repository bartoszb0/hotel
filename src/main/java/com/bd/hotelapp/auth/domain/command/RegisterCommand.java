package com.bd.hotelapp.auth.domain.command;

public record RegisterCommand(
        String firstName,
        String lastName,
        String email,
        String password) {
}
