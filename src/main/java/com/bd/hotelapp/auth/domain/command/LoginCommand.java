package com.bd.hotelapp.auth.domain.command;

public record LoginCommand(
        String email,
        String password
) {
}
