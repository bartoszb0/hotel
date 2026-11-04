package com.bd.hotelapp.guest.domain.dto;

import java.util.UUID;

public record GuestResponse(
        UUID id,
        String firstName,
        String lastName,
        String email
) {
}
