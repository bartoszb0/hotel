package com.bd.hotelapp.roomtype.mapper;

import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.command.UpdateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.dto.CreateRoomTypeRequest;
import com.bd.hotelapp.roomtype.domain.dto.RoomTypeResponse;
import com.bd.hotelapp.roomtype.domain.dto.UpdateRoomTypeRequest;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    RoomTypeResponse toResponse(RoomType roomType);

    CreateRoomTypeCommand toCommand(CreateRoomTypeRequest createRoomTypeRequest);

    UpdateRoomTypeCommand toCommand(UpdateRoomTypeRequest updateRoomTypeRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromCommand(UpdateRoomTypeCommand updateRoomTypeCommand, @MappingTarget RoomType roomType);
}
