package com.bd.hotelapp.roomtype.domain.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateRoomTypeRequest(

        @NotBlank(message = "Room type name can not be empty")
        @Size(max = 50, message = "Room type name can not be bigger than 50")
        String name,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity cannot be smaller than 1")
        Integer capacity,

        @NotNull(message = "Price per night is required")
        @Positive(message = "Price par night must be bigger than 0")
        @Digits(integer = 8, fraction = 2, message = "Price per night must have at most 8 digits and 2 decimal places")
        BigDecimal pricePerNight
) {
}
