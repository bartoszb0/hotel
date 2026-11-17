package com.bd.hotelapp.user.domain.dto;

import com.bd.hotelapp.user.domain.entity.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        UserRole role
) {
}
