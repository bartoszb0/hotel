package com.bd.hotelapp.roomtype.service.impl;

import com.bd.hotelapp.roomtype.domain.command.CreateRoomTypeCommand;
import com.bd.hotelapp.roomtype.domain.entity.RoomType;
import com.bd.hotelapp.roomtype.exception.RoomTypeAlreadyExistsException;
import com.bd.hotelapp.roomtype.repository.RoomTypeRepository;
import com.bd.hotelapp.roomtype.service.RoomTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RomTypeServiceImpl(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    public List<RoomType> getRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Override
    public RoomType getRoomType(UUID id) {
        return null;
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
    public void deleteRoomType(UUID id) {

    }
}
