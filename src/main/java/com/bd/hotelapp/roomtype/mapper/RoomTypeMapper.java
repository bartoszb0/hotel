package com.bd.hotelapp.roomtype.mapper;

import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.dto.CreateRoomTypeRequest;
import com.bd.hotelapp.roomtype.domain.dto.RoomTypeResponse;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    RoomTypeResponse toResponse(RoomType roomType);

    CreateRoomTypeCommand toCommand(CreateRoomTypeRequest createRoomTypeRequest);
}
