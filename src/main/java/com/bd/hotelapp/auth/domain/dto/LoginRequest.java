package com.bd.hotelapp.auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Size(max = 255, message = "Email cannot be longer than 255 characters")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(max = 72, message = "Password cannot be longer than 72 characters")
        String password
) {
}
