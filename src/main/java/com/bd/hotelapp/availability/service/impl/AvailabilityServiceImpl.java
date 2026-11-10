package com.bd.hotelapp.availability.service.impl;

import com.bd.hotelapp.availability.exception.InvalidSearchDatesException;
import com.bd.hotelapp.availability.service.AvailabilityService;
import com.bd.hotelapp.availability.specification.RoomSearchSpecifications;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.service.RoomService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final RoomService roomService;

    public AvailabilityServiceImpl(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public List<Room> searchRooms(UUID roomTypeId, Integer minCapacity, BigDecimal maxPrice, LocalDate from, LocalDate to) {
        validateFromToDates(from, to);

        List<Specification<Room>> specifications = new ArrayList<>();

        if (roomTypeId != null) specifications.add(RoomSearchSpecifications.hasRoomType(roomTypeId));
        if (minCapacity != null) specifications.add(RoomSearchSpecifications.hasMinCapacity(minCapacity));
        if (maxPrice != null) specifications.add(RoomSearchSpecifications.hasMaxPrice(maxPrice));
        if (from != null && to != null) specifications.add(RoomSearchSpecifications.isAvailableBetween(from, to));

        Specification<Room> roomSpecification = Specification.allOf(specifications);

        return roomService.findRooms(roomSpecification);
    }

    private void validateFromToDates(LocalDate from, LocalDate to) {
        if ((from == null) != (to == null)) {
            throw new InvalidSearchDatesException("Both 'from' and 'to' must be provided together");
        }

        if (from != null && !to.isAfter(from)) {
            throw new InvalidSearchDatesException("'to' date must be after 'from' date");
        }
    }
}
