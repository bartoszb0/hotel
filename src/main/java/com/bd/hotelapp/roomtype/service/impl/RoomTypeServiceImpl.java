package com.bd.hotelapp.roomtype.service.impl;

import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.command.UpdateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import com.bd.hotelapp.roomtype.exception.RoomTypeAlreadyExistsException;
import com.bd.hotelapp.roomtype.exception.RoomTypeNotFoundException;
import com.bd.hotelapp.roomtype.mapper.RoomTypeMapper;
import com.bd.hotelapp.roomtype.repository.RoomTypeRepository;
import com.bd.hotelapp.roomtype.service.RoomTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository, RoomTypeMapper roomTypeMapper) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomTypeMapper = roomTypeMapper;
    }

    @Override
    public List<RoomType> getRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Override
    public RoomType getRoomType(UUID id) {
        return roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeNotFoundException(id));
    }

    @Override
    public RoomType createRoomType(CreateRoomTypeCommand createRoomTypeCommand) {
        if (roomTypeRepository.existsByNameIgnoreCase(createRoomTypeCommand.name())) {
            throw new RoomTypeAlreadyExistsException(createRoomTypeCommand.name());
        }

        RoomType roomType = new RoomType(
                createRoomTypeCommand.name(),
                createRoomTypeCommand.capacity(),
                createRoomTypeCommand.pricePerNight()
        );

        return roomTypeRepository.save(roomType);
    }

    @Override
    public RoomType updateRoomType(UUID id, UpdateRoomTypeCommand updateRoomTypeCommand) {
        RoomType roomType = getRoomType(id);

        if (updateRoomTypeCommand.name() != null
                && roomTypeRepository.existsByNameIgnoreCaseAndIdIsNot(updateRoomTypeCommand.name(), id)) {
            throw new RoomTypeAlreadyExistsException(updateRoomTypeCommand.name());
        }

        roomTypeMapper.updateFromCommand(updateRoomTypeCommand, roomType);

        return roomTypeRepository.save(roomType);
    }

    @Override
    public void deleteRoomType(UUID id) {
        RoomType roomType = getRoomType(id);
        roomTypeRepository.delete(roomType);
    }
}
