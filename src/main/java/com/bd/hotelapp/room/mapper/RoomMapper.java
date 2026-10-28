package com.bd.hotelapp.room.mapper;

import com.bd.hotelapp.room.domain.command.CreateRoomCommand;
import com.bd.hotelapp.room.domain.dto.CreateRoomRequest;
import com.bd.hotelapp.room.domain.dto.RoomResponse;
import com.bd.hotelapp.room.domain.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomResponse toResponse(Room room);

    CreateRoomCommand toCommand(CreateRoomRequest createRoomRequest);
}
