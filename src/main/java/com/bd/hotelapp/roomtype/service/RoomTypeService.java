package com.bd.hotelapp.roomtype.service;

import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;

import java.util.List;
import java.util.UUID;

public interface RoomTypeService {

    List<RoomType> getRoomTypes();

    RoomType getRoomType(UUID id);

    RoomType createRoomType(CreateRoomTypeCommand createRoomTypeCommand);

    // update

    void deleteRoomType(UUID id);

}
