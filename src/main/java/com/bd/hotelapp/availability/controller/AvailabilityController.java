package com.bd.hotelapp.availability.controller;

import com.bd.hotelapp.availability.service.AvailabilityService;
import com.bd.hotelapp.common.web.ApiPaths;
import com.bd.hotelapp.room.domain.dto.RoomResponse;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.mapper.RoomMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.V1 + "/rooms/available")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final RoomMapper roomMapper;

    public AvailabilityController(AvailabilityService availabilityService, RoomMapper roomMapper) {
        this.availabilityService = availabilityService;
        this.roomMapper = roomMapper;
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> searchAvailableRooms(
            @RequestParam(required = false) UUID roomTypeId,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<Room> rooms = availabilityService.searchRooms(roomTypeId, minCapacity, maxPrice, from, to);
        return ResponseEntity.ok(rooms.stream().map(roomMapper::toResponse).toList());
    }
}