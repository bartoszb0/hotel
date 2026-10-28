package com.bd.hotelapp.roomtype.domain.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateRoomTypeRequest(

        @Pattern(regexp = ".*\\S.*", message = "Room type name cannot be blank")
        @Size(min = 1, max = 50, message = "Room type name length cannot be smaller than 1 and be bigger than 50")
        String name,

        @Min(value = 1, message = "Capacity cannot be smaller than 1")
        Integer capacity,

        @Positive(message = "Price per night must be bigger than 0")
        @Digits(integer = 8, fraction = 2, message = "Price per night must have at most 8 digits and 2 decimal places")
        BigDecimal pricePerNight
) {
}
