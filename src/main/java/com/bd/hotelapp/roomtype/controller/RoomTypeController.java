package com.bd.hotelapp.roomtype.controller;

import com.bd.hotelapp.common.web.ApiPaths;
import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.dto.CreateRoomTypeRequest;
import com.bd.hotelapp.roomtype.domain.dto.RoomTypeResponse;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import com.bd.hotelapp.roomtype.mapper.RoomTypeMapper;
import com.bd.hotelapp.roomtype.service.RoomTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.V1 + "/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;
    private final RoomTypeMapper roomTypeMapper;

    public RoomTypeController(RoomTypeService roomTypeService, RoomTypeMapper roomTypeMapper) {
        this.roomTypeService = roomTypeService;
        this.roomTypeMapper = roomTypeMapper;
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeResponse>> getRoomTypes() {
        List<RoomType> roomTypes = roomTypeService.getRoomTypes();
        List<RoomTypeResponse> roomTypesResponse = roomTypes.stream().map(roomTypeMapper::toResponse).toList();
        return new ResponseEntity<>(roomTypesResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoomTypeResponse> createRoomType(@RequestBody @Valid CreateRoomTypeRequest createRoomTypeRequest) {
        CreateRoomTypeCommand roomTypeCommand = roomTypeMapper.toCommand(createRoomTypeRequest);
        RoomType roomType = roomTypeService.createRoomType(roomTypeCommand);
        RoomTypeResponse roomTypeResponse = roomTypeMapper.toResponse(roomType);
        return new ResponseEntity<>(roomTypeResponse, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{roomTypeId}")
    public ResponseEntity<RoomTypeResponse> getRoomType(@PathVariable UUID roomTypeId) {
        RoomType roomType = roomTypeService.getRoomType(roomTypeId);
        RoomTypeResponse roomTypeResponse = roomTypeMapper.toResponse(roomType);
        return new ResponseEntity<>(roomTypeResponse, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{roomTypeId}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable UUID roomTypeId) {
        roomTypeService.deleteRoomType(roomTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
