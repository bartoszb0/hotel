package com.bd.hotelapp.room.mapper;

import com.bd.hotelapp.room.domain.command.CreateRoomCommand;
import com.bd.hotelapp.room.domain.command.UpdateRoomCommand;
import com.bd.hotelapp.room.domain.dto.CreateRoomRequest;
import com.bd.hotelapp.room.domain.dto.RoomResponse;
import com.bd.hotelapp.room.domain.dto.UpdateRoomRequest;
import com.bd.hotelapp.room.domain.entity.Room;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomResponse toResponse(Room room);

    CreateRoomCommand toCommand(CreateRoomRequest createRoomRequest);

    UpdateRoomCommand toCommand(UpdateRoomRequest updateRoomRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "roomType", ignore = true)
    void updateFromCommand(UpdateRoomCommand updateRoomCommand, @MappingTarget Room room);
}
