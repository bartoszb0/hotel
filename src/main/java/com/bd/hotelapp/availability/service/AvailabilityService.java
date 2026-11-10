package com.bd.hotelapp.availability.service;

import com.bd.hotelapp.room.domain.entity.Room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {

    List<Room> searchRooms(UUID roomTypeId, Integer minCapacity, BigDecimal maxPrice, LocalDate from, LocalDate to);
}
