package com.bd.hotelapp.room.controller;

import com.bd.hotelapp.common.web.ApiPaths;
import com.bd.hotelapp.room.domain.command.CreateRoomCommand;
import com.bd.hotelapp.room.domain.dto.CreateRoomRequest;
import com.bd.hotelapp.room.domain.dto.RoomResponse;
import com.bd.hotelapp.room.domain.entity.Room;
import com.bd.hotelapp.room.mapper.RoomMapper;
import com.bd.hotelapp.room.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.V1 + "/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public RoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms() {
        List<Room> rooms = roomService.getRooms();
        List<RoomResponse> roomsResponse = rooms.stream().map(roomMapper::toResponse).toList();
        return new ResponseEntity<>(roomsResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@RequestBody @Valid CreateRoomRequest createRoomRequest) {
        CreateRoomCommand createRoomCommand = roomMapper.toCommand(createRoomRequest);
        Room room = roomService.createRoom(createRoomCommand);
        RoomResponse roomResponse = roomMapper.toResponse(room);
        return new ResponseEntity<>(roomResponse, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable UUID roomId) {
        Room room = roomService.getRoom(roomId);
        RoomResponse roomResponse = roomMapper.toResponse(room);
        return new ResponseEntity<>(roomResponse, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
