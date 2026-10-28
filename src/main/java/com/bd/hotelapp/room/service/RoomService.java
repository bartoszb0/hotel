package com.bd.hotelapp.room.service;

import com.bd.hotelapp.room.domain.command.CreateRoomCommand;
import com.bd.hotelapp.room.domain.entity.Room;

import java.util.List;
import java.util.UUID;

public interface RoomService {

    List<Room> getRooms();

    Room createRoom(CreateRoomCommand createRoomCommand);

    Room getRoom(UUID id);

//    Room updateRoom()

    void deleteRoom(UUID id);
}
